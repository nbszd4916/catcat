package com.catdorm.service.visitor;

import com.catdorm.model.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

// 订单元素（实现Element接口）
@Data
@AllArgsConstructor
public class OrderElement implements Element {
    private Order order;
    private List<CatElement> catElements;

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitOrder(this);
    }
}