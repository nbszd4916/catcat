package com.catdorm.service;

import com.catdorm.model.Cat;
import com.catdorm.model.Order;
import com.catdorm.repository.CatRepository;
import com.catdorm.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 订单业务逻辑层
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CatRepository catRepository;

    // 保存订单和关联的猫咪信息
    public void saveOrder(Order order, List<Cat> cats) {
        orderRepository.save(order);
        cats.forEach(cat -> {
            cat.setOrderId(order.getId());
            catRepository.save(cat);
        });
    }

    // 判断用户是否有处理中的订单
    public boolean hasProcessingOrder(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().anyMatch(o -> "processing".equals(o.getStatus()));
    }

    // 根据咪码查询猫咪
    public Cat getCatByMiCode(String miCode) {
        return catRepository.findByMiCode(miCode);
    }

    // 判断猫咪是否属于当前用户
    public boolean isCatBelongToUser(Long catId, Long userId) {
        Cat cat = catRepository.findById(catId).orElse(null);
        if (cat == null) return false;
        Order order = orderRepository.findById(cat.getOrderId()).orElse(null);
        return order != null && order.getUserId().equals(userId);
    }

    // 根据用户ID查询订单列表（关联猫咪信息）
    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        orders.forEach(o -> o.setCats(catRepository.findByOrderId(o.getId())));
        return orders;
    }

    // 根据订单状态查询订单
    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        orders.forEach(o -> o.setCats(catRepository.findByOrderId(o.getId())));
        return orders;
    }

    // 获取所有订单
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        orders.forEach(o -> o.setCats(catRepository.findByOrderId(o.getId())));
        return orders;
    }

    // 根据ID获取订单
    public Order getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order != null) {
            order.setCats(catRepository.findByOrderId(orderId));
        }
        return order;
    }

    // 更新订单状态
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        orderRepository.save(order);
    }
    
    // 更新订单(保存整个订单对象)
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }
    
    // 根据用户ID和订单状态查询订单
    public List<Order> getOrdersByUserIdAndStatus(Long userId, String status) {
        List<Order> orders = orderRepository.findAll().stream()
            .filter(order -> order.getUserId().equals(userId) && order.getStatus().equals(status))
            .collect(java.util.stream.Collectors.toList());
        
        // 加载每个订单的猫咪数据
        for (Order order : orders) {
            List<Cat> cats = catRepository.findByOrderId(order.getId());
            order.setCats(cats);
        }
        
        return orders;
    }

    // 删除订单
    public void deleteOrder(Long orderId) {
        // 先删除关联的猫咪
        List<Cat> cats = catRepository.findByOrderId(orderId);
        catRepository.deleteAll(cats);
        // 再删除订单
        orderRepository.deleteById(orderId);
    }
}