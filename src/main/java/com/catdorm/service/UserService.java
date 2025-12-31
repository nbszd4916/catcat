package com.catdorm.service;

import com.catdorm.model.User;
import com.catdorm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// 用户业务逻辑层
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    // 根据用户名查询用户
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // 用户注册
    public User registerUser(User user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 检查是否为管理员注册
        if (user.getIsAdmin() == null) {
            user.setIsAdmin(0);
        }
        if (user.getIsAdmin() == 0) {
            user.setIsApproved(1); // 普通用户直接通过
        } else {
            user.setIsApproved(0); // 管理员需要审核
        }
        // 设置注册时间
        if (user.getCreateTime() == null) {
            user.setCreateTime(new java.util.Date());
        }
        return userRepository.save(user);
    }

    // 获取待审核用户列表
    public List<User> getPendingUsers() {
        return userRepository.findByIsApproved(0);
    }

    // 审核用户
    public void approveUser(Long userId, Integer approve) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setIsApproved(approve);
        userRepository.save(user);
    }
    
    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // 根据ID获取用户
    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
    
    // 更新用户
    public void updateUser(User user) {
        userRepository.save(user);
    }
    
    // 删除用户
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}