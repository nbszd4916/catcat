
// 店铺实体
package com.catdorm.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String description; // 店铺介绍
    private String images; // 店铺图片(逗号分隔多张)
}