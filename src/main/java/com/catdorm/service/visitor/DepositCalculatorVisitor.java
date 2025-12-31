package com.catdorm.service.visitor;

import java.math.BigDecimal;

/**
 * 访问者模式 - 定金计算访问者
 * 
 * 费用计算规则:
 * 1. 基础寄养费用 = 35元/只/天
 * 2. 接送服务费 = 80元(需要接送时)
 * 3. 定金 = 接送费的50% + 基础寄养费的20%
 */
public class DepositCalculatorVisitor implements Visitor<BigDecimal> {
    @Override
    public BigDecimal visitOrder(OrderElement orderElement) {
        int catCount = orderElement.getCatElements().size();
        int duration = orderElement.getOrder().getDuration();
        int needPickup = orderElement.getOrder().getNeedPickup();
        
        // 基础寄养费用: 35元/只/天
        BigDecimal baseFee = new BigDecimal(catCount * 35 * duration);
        
        // 接送服务费用: 80元(需要时)
        BigDecimal pickupFee = needPickup == 1 ? new BigDecimal(80) : BigDecimal.ZERO;
        
        // 定金 = 接送费的50% + 基础寄养费的20%
        BigDecimal depositFromPickup = pickupFee.multiply(new BigDecimal("0.5"));
        BigDecimal depositFromBase = baseFee.multiply(new BigDecimal("0.2"));
        
        return depositFromPickup.add(depositFromBase);
    }

    @Override
    public BigDecimal visitCat(CatElement catElement) {
        return BigDecimal.ZERO;
    }
}