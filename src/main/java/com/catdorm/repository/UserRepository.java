package com.catdorm.repository;

import com.catdorm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 用户数据库访问接口（JPA自动实现CRUD）
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByIsApproved(Integer isApproved);
}