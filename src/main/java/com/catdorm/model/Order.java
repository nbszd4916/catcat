
// 订单实体
package com.catdorm.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 订单实体类
 * 
 * 订单状态说明:
 * - cancelled: 已取消(用户未支付定金的订单)
 * - pickup_pending: 待接送(用户已支付定金,等待管理员接送)
 * - arrived: 已到店(猫咪已被接送到店)
 * - completed: 已完成(寄养服务结束)
 * - refunded: 已退款(订单取消并退款)
 */
@Data
@Entity
@Table(name = "`order`") // order是MySQL关键字，需转义
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 联系方式 */
    private String contact;
    
    /** 取猫地址 */
    private String pickAddress;
    
    /** 寄养开始时间 */
    private Date startTime;
    
    /** 寄养时长(天) */
    private Integer duration;
    
    /** 是否需要接送服务: 0-不需要, 1-需要 */
    private Integer needPickup;
    
    /** 送猫地址 */
    private String sendAddress;
    
    /** 定金总额 */
    private BigDecimal totalDeposit;
    
    /** 尾款金额 */
    private BigDecimal finalPayment;
    
    /** 额外收费金额 */
    private BigDecimal extraCharge;
    
    /** 额外收费理由 */
    private String extraChargeReason;
    
    /** 是否已支付尾款: 0-未支付, 1-已支付 */
    @Column(columnDefinition = "int default 0")
    private Integer finalPaymentPaid;
    
    /** 订单状态: cancelled/pickup_pending/arrived/completed/refunded */
    private String status;
    
    /** 处理子状态 */
    private String processSubstatus;
    
    /** 创建时间 */
    private Date createTime;

    // 关联猫咪
    @Transient // 不映射到数据库
    private List<com.catdorm.model.Cat> cats;
    @Transient
    private User user;
}
