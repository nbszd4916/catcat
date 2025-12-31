package com.catdorm.service.visitor;

// 访问者模式的访问者接口（定义对元素的操作）
public interface Visitor<T> {
    T visitOrder(OrderElement orderElement);
    T visitCat(CatElement catElement);
}