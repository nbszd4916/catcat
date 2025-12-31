package com.catdorm.service.visitor;

import com.catdorm.model.Cat;
import lombok.AllArgsConstructor;
import lombok.Data;

// 猫咪元素（实现Element接口）
@Data
@AllArgsConstructor
public class CatElement implements Element {
    private Cat cat;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitCat(this);
    }
}