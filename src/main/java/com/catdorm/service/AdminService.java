package com.catdorm.service;

import com.catdorm.model.Cat;
import com.catdorm.model.Order;
import com.catdorm.repository.CatRepository;
import com.catdorm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 管理员业务逻辑层
@Service
public class AdminService {
    @Autowired
    private CatRepository catRepository;
    @Autowired
    private OrderRepository orderRepository;

    // 获取所有在舍猫咪(订单状态为processing的猫)
    public List<Cat> getAllCatsInStore() {
        List<Order> processingOrders = orderRepository.findByStatus("processing");
        return processingOrders.stream()
                .flatMap(order -> catRepository.findByOrderId(order.getId()).stream())
                .toList();
    }
}
