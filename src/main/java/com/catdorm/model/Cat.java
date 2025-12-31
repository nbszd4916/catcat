// 猫咪实体
package com.catdorm.model;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "cat")
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String catName;
    private String breed;
    private BigDecimal weight;
    private Integer age;
    private String image;
    private String remarks;
    private String miCode;
    private Long storeId;
    private Long staffId;

    @Transient
    private Order order;
    @Transient
    private User user;
    @Transient
    private com.catdorm.model.Store store;
}
