package com.catdorm.repository;

import com.catdorm.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;

// 店铺数据库访问接口
public interface StoreRepository extends JpaRepository<Store, Long> {
}
