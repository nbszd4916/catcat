package com.catdorm.service.facade;

import com.catdorm.model.Cat;
import com.catdorm.model.Order;
import com.catdorm.model.User;
import com.catdorm.service.OrderService;
import com.catdorm.service.visitor.CatElement;
import com.catdorm.service.visitor.DepositCalculatorVisitor;
import com.catdorm.service.visitor.MiCodeGeneratorVisitor;
import com.catdorm.service.visitor.OrderElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 外观模式(Facade Pattern) - 订单外观服务
 * 
 * 作用: 为复杂的订单处理子系统提供一个简化的统一接口
 * 优点: 
 * 1. 简化客户端调用,隐藏系统复杂性
 * 2. 降低客户端与子系统的耦合度
 * 3. 更好地分层,易于维护
 */
@Service
public class OrderFacade {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 处理订单申请的外观方法
     * 整合了订单创建、定金计算、咪码生成、数据保存等多个步骤
     * 
     * @param order 订单信息
     * @param cats 猫咪列表
     * @param user 当前用户
     * @return 处理后的订单
     */
    public Order processOrderApplication(Order order, List<Cat> cats, User user) {
        // 步骤1: 设置订单基础信息
        order.setUserId(user.getId());
        order.setCreateTime(new Date());
        order.setStatus("pickup_pending"); // 初始状态为待接送(用户已提交申请并支付定金)
        
        // 步骤2: 使用访问者模式计算定金
        BigDecimal deposit = calculateDeposit(order, cats);
        order.setTotalDeposit(deposit);
        
        // 步骤3: 计算尾款（基础尾款 = 总费用 - 定金）
        BigDecimal totalCost = calculateTotalCost(order, cats);
        BigDecimal finalPayment = totalCost.subtract(deposit);
        order.setFinalPayment(finalPayment);
        order.setFinalPaymentPaid(0); // 初始未支付
        
        // 步骤4: 为每只猫生成咪码(使用访问者模式)
        generateMiCodesForCats(cats);
        
        // 步骤5: 保存订单和猫咪信息到数据库
        orderService.saveOrder(order, cats);
        
        return order;
    }
    
    /**
     * 使用访问者模式计算订单定金
     * 
     * @param order 订单
     * @param cats 猫咪列表
     * @return 定金金额
     */
    private BigDecimal calculateDeposit(Order order, List<Cat> cats) {
        // 将猫咪列表转换为访问者模式的元素列表
        List<CatElement> catElements = cats.stream()
                .map(CatElement::new)
                .collect(Collectors.toList());
        
        // 创建订单元素(包含订单和猫咪信息)
        OrderElement orderElement = new OrderElement(order, catElements);
        
        // 使用定金计算访问者计算定金
        return orderElement.accept(new DepositCalculatorVisitor());
    }
    
    /**
     * 计算订单总费用
     * 费用规则：
     * 1. 基础寄养费用 = 35元/只/天
     * 2. 接送服务费 = 80元(需要接送时)
     * 
     * @param order 订单
     * @param cats 猫咪列表
     * @return 总费用
     */
    private BigDecimal calculateTotalCost(Order order, List<Cat> cats) {
        int catCount = cats.size();
        int duration = order.getDuration();
        int needPickup = order.getNeedPickup();
        
        // 基础寄养费用: 35元/只/天
        BigDecimal baseFee = new BigDecimal(catCount * 35 * duration);
        
        // 接送服务费用: 80元(需要时)
        BigDecimal pickupFee = needPickup == 1 ? new BigDecimal(80) : BigDecimal.ZERO;
        
        // 总费用 = 基础寄养费 + 接送费
        return baseFee.add(pickupFee);
    }
    
    /**
     * 为猫咪列表生成唯一咪码
     * 
     * @param cats 猫咪列表
     */
    private void generateMiCodesForCats(List<Cat> cats) {
        // 使用访问者模式为每只猫生成咪码
        MiCodeGeneratorVisitor miCodeVisitor = new MiCodeGeneratorVisitor();
        
        for (Cat cat : cats) {
            CatElement catElement = new CatElement(cat);
            String miCode = catElement.accept(miCodeVisitor);
            cat.setMiCode(miCode);
        }
    }
    
    /**
     * 计算订单费用明细
     * 
     * @param order 订单
     * @param cats 猫咪列表  
     * @return 费用明细说明
     */
    public String calculateFeeDetails(Order order, List<Cat> cats) {
        int catCount = cats.size();
        int duration = order.getDuration();
        int dailyFee = 50; // 每只猫每天50元
        
        int totalFee = catCount * dailyFee * duration;
        int deposit = totalFee / 2; // 定金为总费用的50%
        
        StringBuilder details = new StringBuilder();
        details.append("费用明细:\n");
        details.append("猫咪数量: ").append(catCount).append(" 只\n");
        details.append("寄养天数: ").append(duration).append(" 天\n");
        details.append("每只猫每天: ").append(dailyFee).append(" 元\n");
        details.append("总费用: ").append(totalFee).append(" 元\n");
        details.append("定金(50%): ").append(deposit).append(" 元\n");
        
        return details.toString();
    }
}
