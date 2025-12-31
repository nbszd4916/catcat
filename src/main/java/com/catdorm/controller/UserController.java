package com.catdorm.controller;

import com.catdorm.dto.OrderSubmitDTO;
import com.catdorm.model.Cat;
import com.catdorm.model.Order;
import com.catdorm.model.User;
import com.catdorm.service.OrderService;
import com.catdorm.service.UserService;
import com.catdorm.service.facade.OrderFacade;
import com.catdorm.util.QRCodeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderFacade orderFacade; // 外观模式:简化订单处理

    // 登录页面（GET请求）
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // 对应templates/login.html
    }

    // 处理登录请求（POST请求）
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, 
                       @RequestParam(defaultValue = "0") Integer isAdmin,
                       HttpSession session, Model model) {
        User user = userService.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
        // 检查用户类型是否匹配
        if (!user.getIsAdmin().equals(isAdmin)) {
            model.addAttribute("error", "用户类型不匹配");
            return "login";
        }
        // 检查是否已审核
        if (user.getIsApproved() == 0) {
            model.addAttribute("error", "账号待审核，请耐心等待");
            return "login";
        }
        if (user.getIsApproved() == 2) {
            model.addAttribute("error", "账号已被拒绝");
            return "login";
        }
        session.setAttribute("user", user); // 与代码中session.getAttribute("user")对应
        
        // 根据用户类型跳转
        if (user.getIsAdmin() == 1) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/dashboard";
        }
    }

    // 注册处理
    @PostMapping("/register")
    public String register(@RequestParam String username, 
                          @RequestParam String password,
                          @RequestParam String nickname,
                          @RequestParam(defaultValue = "0") Integer isAdmin,
                          Model model) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setIsAdmin(isAdmin);
            userService.registerUser(user);
            
            if (isAdmin == 1) {
                model.addAttribute("success", "管理员注册成功，请等待审核");
            } else {
                model.addAttribute("success", "注册成功，请登录");
            }
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    // 用户仪表盘
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getIsAdmin() == 1) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/dashboard";
    }

    // 申请入住页面
    @GetMapping("/apply-boarding")
    public String applyBoarding(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getIsAdmin() == 1) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "user/apply_boarding";
    }

    // 提交入住申请 - 使用DTO接收表单数据,使用外观模式简化复杂流程
    @PostMapping("/submit-order")
    public String submitOrder(@ModelAttribute OrderSubmitDTO orderDTO,
                            HttpSession session, Model model) {
        // 获取当前登录用户
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            // 步骤1: 将DTO转换为Order实体
            Order order = new Order();
            order.setContact(orderDTO.getContact());
            order.setPickAddress(orderDTO.getPickAddress());
            
            // 将字符串日期转换为Date对象
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            order.setStartTime(sdf.parse(orderDTO.getStartTime()));
            
            order.setDuration(orderDTO.getDuration());
            order.setNeedPickup(orderDTO.getNeedPickup());
            order.setSendAddress(orderDTO.getSendAddress());
            
            // 步骤2: 将DTO中的猫咪列表转换为Cat实体列表
            List<Cat> cats = new ArrayList<>();
            if (orderDTO.getCats() != null) {
                for (OrderSubmitDTO.CatDTO catDTO : orderDTO.getCats()) {
                    Cat cat = new Cat();
                    cat.setCatName(catDTO.getCatName());
                    cat.setBreed(catDTO.getBreed());
                    // 将Double转换为BigDecimal
                    cat.setWeight(catDTO.getWeight() != null ? 
                        java.math.BigDecimal.valueOf(catDTO.getWeight()) : null);
                    cat.setAge(catDTO.getAge());
                    cat.setImage(catDTO.getImage());
                    cat.setRemarks(catDTO.getRemarks());
                    cats.add(cat);
                }
            }
            
            // 步骤3: 使用外观模式处理订单申请(整合了定金计算、咪码生成等复杂步骤)
            orderFacade.processOrderApplication(order, cats, user);
            
            // 跳转到订单列表页面
            return "redirect:/user/profile";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "订单提交失败: " + e.getMessage());
            return "user/apply_boarding";
        }
    }

    // 看看猫页面 - 显示用户当前已到店的猫咪直播
    @GetMapping("/view-cats")
    public String viewCats(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取用户所有"已到店"状态的订单
        List<Order> arrivedOrders = orderService.getOrdersByUserIdAndStatus(user.getId(), "arrived");
        
        // 从订单中提取所有猫咪
        List<Cat> cats = new ArrayList<>();
        for (Order order : arrivedOrders) {
            if (order.getCats() != null) {
                cats.addAll(order.getCats());
            }
        }
        
        model.addAttribute("user", user);
        model.addAttribute("cats", cats);
        return "user/view_cats";
    }

    // 带咪回家（生成二维码）
    @PostMapping("/generate-qrcode")
    @ResponseBody
    public String generateQRCode(@RequestParam String miCode, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "error:未登录";
        }
        // 验证咪码是否属于当前用户
        Cat cat = orderService.getCatByMiCode(miCode);
        if (cat == null || !orderService.isCatBelongToUser(cat.getId(), user.getId())) {
            return "error:咪码错误或非本人猫咪";
        }
        // 生成二维码Base64
        return QRCodeUtil.generateQRCodeBase64(miCode, 300, 300);
    }

    // 用户个人主页（我的订单）
    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || user.getIsAdmin() == 1) {
            return "redirect:/login";
        }
        // 获取用户所有订单
        List<Order> orders = orderService.getOrdersByUserId(user.getId());
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "user/profile";
    }
    
    // 删除订单
    @PostMapping("/delete-order")
    @ResponseBody
    public String deleteOrder(@RequestParam Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "error:未登录";
        }
        
        try {
            // 验证订单是否属于当前用户
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "error:订单不存在";
            }
            if (!order.getUserId().equals(user.getId())) {
                return "error:无权删除此订单";
            }
            
            orderService.deleteOrder(orderId);
            return "success:订单已删除";
        } catch (Exception e) {
            return "error:删除失败";
        }
    }
    
    // 支付尾款
    @PostMapping("/pay-final-payment")
    @ResponseBody
    public String payFinalPayment(@RequestParam Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "error:未登录";
        }
        
        try {
            // 验证订单是否属于当前用户
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "error:订单不存在";
            }
            if (!order.getUserId().equals(user.getId())) {
                return "error:无权操作此订单";
            }
            
            // 检查订单状态
            if (!"arrived".equals(order.getStatus())) {
                return "error:订单状态不允许支付尾款";
            }
            
            // 检查是否已支付
            if (order.getFinalPaymentPaid() == 1) {
                return "error:尾款已支付，请勿重复操作";
            }
            
            // 模拟支付成功，更新支付状态
            order.setFinalPaymentPaid(1);
            order.setStatus("completed"); // 支付尾款后订单状态改为"已完成"
            orderService.updateOrder(order);
            
            return "success:尾款支付成功，订单已完成";
        } catch (Exception e) {
            return "error:支付失败";
        }
    }
    
    // 退出登录
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清除session中的用户信息
        session.removeAttribute("user");
        // 使session失效
        session.invalidate();
        // 重定向到登录页面
        return "redirect:/login";
    }
}