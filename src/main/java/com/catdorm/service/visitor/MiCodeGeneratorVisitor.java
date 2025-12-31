package com.catdorm.service.visitor;

import java.util.UUID;

// 生成唯一咪码
public class MiCodeGeneratorVisitor implements Visitor<String> {
    @Override
    public String visitOrder(OrderElement orderElement) {
        return null; // 订单级生成无意义
    }

    @Override
    public String visitCat(CatElement catElement) {
        // 生成8位唯一咪码：CAT + 随机大写字符串
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "CAT" + uuid;
    }
}