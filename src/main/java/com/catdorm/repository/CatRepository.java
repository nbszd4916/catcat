package com.catdorm.repository;

import com.catdorm.model.Cat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// 猫咪数据库访问接口
public interface CatRepository extends JpaRepository<Cat, Long> {
    Cat findByMiCode(String miCode);
    List<Cat> findByOrderId(Long orderId);
}