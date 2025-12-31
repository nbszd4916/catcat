package com.catdorm.service.visitor;

// 访问者模式的元素接口（被访问的对象：订单、猫咪）
public interface Element {
    <T> T accept(Visitor<T> visitor);
}