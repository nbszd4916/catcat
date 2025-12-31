package com.catdorm.repository;

import com.catdorm.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 订单数据库访问接口
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByStatus(String status);
}