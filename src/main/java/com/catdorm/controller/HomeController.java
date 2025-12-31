package com.catdorm.controller;

import com.catdorm.model.Store;
import com.catdorm.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

// 首页控制器
@Controller
public class HomeController {
    @Autowired
    private StoreService storeService;

    // 首页
    @GetMapping("/")
    public String home(Model model) {
        List<Store> stores = storeService.getAllStores();
        model.addAttribute("stores", stores);
        return "index";
    }

    // 登录页面
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // 注册页面
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
}
