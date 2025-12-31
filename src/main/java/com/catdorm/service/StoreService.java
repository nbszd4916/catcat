package com.catdorm.service;

import com.catdorm.model.Store;
import com.catdorm.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 店铺业务逻辑层
@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;

    // 获取所有店铺
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }

    // 根据ID获取店铺
    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElse(null);
    }

    // 保存店铺
    public Store saveStore(Store store) {
        return storeRepository.save(store);
    }
}
