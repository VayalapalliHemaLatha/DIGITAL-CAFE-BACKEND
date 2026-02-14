package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.OrderStatus;

public class UpdateOrderStatusRequest {

    private OrderStatus status;

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
