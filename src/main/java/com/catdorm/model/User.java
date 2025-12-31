// 用户实体
package com.catdorm.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private Integer isAdmin; // 0=用户，1=管理员
    private Integer isApproved; // 0=未审核，1=已审核
    
    /** 注册时间 */
    private java.util.Date createTime;
}
