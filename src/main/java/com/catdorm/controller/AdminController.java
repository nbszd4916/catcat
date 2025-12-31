package com.catdorm.controller;

import com.catdorm.model.Cat;
import com.catdorm.model.Order;
import com.catdorm.model.User;
import com.catdorm.service.OrderService;
import com.catdorm.service.UserService;
import com.catdorm.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private AdminService adminService;

    // 管理员仪表盘
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getIsAdmin() != 1) {
            return "redirect:/login";
        }
        
        // 获取所有用户(用于显示用户总数)
        List<User> allUsers = userService.getAllUsers();
        
        // 只获取待审核的管理员申请(isAdmin=1 且 isApproved=0)
        List<User> pendingAdmins = allUsers.stream()
            .filter(u -> u.getIsAdmin() == 1 && u.getIsApproved() == 0)
            .collect(java.util.stream.Collectors.toList());
        
        // 获取所有订单按状态分类
        List<Order> unpaidOrders = orderService.getOrdersByStatus("pickup_pending"); // 待接送
        List<Order> processingOrders = orderService.getOrdersByStatus("arrived"); // 已到店
        List<Order> completedOrders = orderService.getOrdersByStatus("completed"); // 已完成
        
        model.addAttribute("admin", admin);
        model.addAttribute("userCount", allUsers.size()); // 用户总数
        model.addAttribute("pendingUsers", pendingAdmins); // 待审核管理员
        model.addAttribute("unpaidOrders", unpaidOrders);
        model.addAttribute("processingOrders", processingOrders);
        model.addAttribute("completedOrders", completedOrders);
        
        return "admin/dashboard";
    }

    // 审核用户注册
    @PostMapping("/approve-user")
    @ResponseBody
    public String approveUser(@RequestParam Long userId, @RequestParam Integer approve) {
        try {
            userService.approveUser(userId, approve);
            return approve == 1 ? "success:审核通过" : "success:已拒绝";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    // 订单管理页面
    @GetMapping("/orders")
    public String orders(HttpSession session, Model model, 
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String search) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getIsAdmin() != 1) {
            return "redirect:/login";
        }
        
        List<Order> orders;
        
        // 搜索功能: 根据订单号或联系人搜索
        if (search != null && !search.isEmpty()) {
            orders = orderService.getAllOrders().stream()
                .filter(order -> 
                    order.getId().toString().contains(search) || 
                    (order.getContact() != null && order.getContact().contains(search))
                )
                .collect(java.util.stream.Collectors.toList());
        } else if (status != null && !status.isEmpty()) {
            orders = orderService.getOrdersByStatus(status);
        } else {
            orders = orderService.getAllOrders();
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("orders", orders);
        model.addAttribute("currentStatus", status);
        model.addAttribute("searchKeyword", search);
        
        return "admin/orders";
    }

    // 接取订单 (废弃的方法,保留以防API兼容)
    @PostMapping("/accept-order")
    @ResponseBody
    public String acceptOrder(@RequestParam Long orderId) {
        try {
            orderService.updateOrderStatus(orderId, "arrived");
            return "success:订单已接取";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }

    // 删除订单
    @PostMapping("/delete-order")
    @ResponseBody
    public String deleteOrder(@RequestParam Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return "success:订单已删除";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
    
    // 增加额外收费
    @PostMapping("/add-extra-charge")
    @ResponseBody
    public String addExtraCharge(@RequestParam Long orderId, 
                                @RequestParam String reason,
                                @RequestParam java.math.BigDecimal amount) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "error:订单不存在";
            }
            
            // 设置额外收费
            order.setExtraCharge(amount);
            order.setExtraChargeReason(reason);
            
            // 更新尾款金额：在原尾款基础上加上额外收费
            if (order.getFinalPayment() != null) {
                order.setFinalPayment(order.getFinalPayment().add(amount));
            } else {
                order.setFinalPayment(amount);
            }
            
            orderService.updateOrder(order);
            return "success:额外收费已添加，尾款已更新";
        } catch (Exception e) {
            return "error:操作失败";
        }
    }

    // 在舍猫管理 - 显示所有"已到店"订单的猫
    @GetMapping("/cats")
    public String cats(HttpSession session, Model model, 
                      @RequestParam(required = false) String miCode,
                      @RequestParam(required = false) String name) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getIsAdmin() != 1) {
            return "redirect:/login";
        }
        
        List<Cat> cats;
        
        // 按咪码搜索
        if (miCode != null && !miCode.isEmpty()) {
            Cat cat = orderService.getCatByMiCode(miCode);
            cats = cat != null ? List.of(cat) : List.of();
        } 
        // 按名字搜索
        else if (name != null && !name.isEmpty()) {
            // 获取所有已到店订单的猫
            List<Order> arrivedOrders = orderService.getOrdersByStatus("arrived");
            cats = arrivedOrders.stream()
                .filter(order -> order.getCats() != null)
                .flatMap(order -> order.getCats().stream())
                .filter(cat -> cat.getCatName() != null && cat.getCatName().contains(name))
                .collect(java.util.stream.Collectors.toList());
        } 
        // 显示所有已到店订单的猫
        else {
            List<Order> arrivedOrders = orderService.getOrdersByStatus("arrived");
            cats = arrivedOrders.stream()
                .filter(order -> order.getCats() != null)
                .flatMap(order -> order.getCats().stream())
                .collect(java.util.stream.Collectors.toList());
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("cats", cats);
        model.addAttribute("searchMiCode", miCode);
        model.addAttribute("searchName", name);
        
        return "admin/cats";
    }

    // 订单详情
    @GetMapping("/order-detail")
    public String orderDetail(@RequestParam Long orderId, HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getIsAdmin() != 1) {
            return "redirect:/login";
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        
        model.addAttribute("admin", admin);
        model.addAttribute("order", order);
        
        return "admin/order_detail";
    }
    
    // 接送完成 - 将订单状态从"pickup_pending"改为"arrived"
    @PostMapping("/pickup-complete")
    @ResponseBody
    public String pickupComplete(@RequestParam Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "error:订单不存在";
            }
            
            // 将订单状态改为"已到店"
            order.setStatus("arrived");
            orderService.updateOrder(order);
            
            return "success:接送完成，订单已更新为已到店";
        } catch (Exception e) {
            return "error:操作失败";
        }
    }
    
    // 取消订单
    @PostMapping("/cancel-order")
    @ResponseBody
    public String cancelOrder(@RequestParam Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "error:订单不存在";
            }
            
            // 将订单状态改为"已取消"
            order.setStatus("cancelled");
            orderService.updateOrder(order);
            
            return "success:订单已取消";
        } catch (Exception e) {
            return "error:操作失败";
        }
    }
    
    // 更新订单状态
    @PostMapping("/update-order-status")
    @ResponseBody
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return "error:订单不存在";
            }
            
            order.setStatus(status);
            orderService.updateOrder(order);
            
            return "success:订单状态已更新";
        } catch (Exception e) {
            return "error:操作失败";
        }
    }
    
    // 用户管理页面
    @GetMapping("/users")
    public String users(HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || admin.getIsAdmin() != 1) {
            return "redirect:/login";
        }
        
        List<User> allUsers = userService.getAllUsers();
        model.addAttribute("admin", admin);
        model.addAttribute("users", allUsers);
        
        return "admin/users";
    }
    
    // 添加用户
    @PostMapping("/add-user")
    @ResponseBody
    public String addUser(@RequestParam String username, 
                         @RequestParam String password,
                         @RequestParam String nickname,
                         @RequestParam(defaultValue = "0") Integer isAdmin) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setNickname(nickname);
            user.setIsAdmin(isAdmin);
            userService.registerUser(user);
            return "success:用户添加成功";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
    
    // 更新用户
    @PostMapping("/update-user")
    @ResponseBody
    public String updateUser(@RequestParam Long userId,
                            @RequestParam String username,
                            @RequestParam String nickname,
                            @RequestParam Integer isAdmin) {
        try {
            User user = userService.getUserById(userId);
            if (user == null) {
                return "error:用户不存在";
            }
            user.setUsername(username);
            user.setNickname(nickname);
            user.setIsAdmin(isAdmin);
            userService.updateUser(user);
            return "success:用户更新成功";
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
    
    // 删除用户
    @PostMapping("/delete-user")
    @ResponseBody
    public String deleteUser(@RequestParam Long userId) {
        try {
            userService.deleteUser(userId);
            return "success:用户删除成功";
        } catch (Exception e) {
            return "error:" + e.getMessage();
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