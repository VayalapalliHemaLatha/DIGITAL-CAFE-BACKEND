package com.cafe.digital_cafe.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Cart-style order (Swiggy/Zomato style): send only items + date/time.
 * Cafe is inferred from items (all must be from the same cafe). Table is optional; if omitted, first table of that cafe is used.
 */
public class CreateOrderFromCartRequest {

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;

    @NotNull
    private LocalDate orderDate;

    @NotNull
    private LocalTime orderTime;

    private Long tableId;

    private Long bookingId;

    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }
    public LocalTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalTime orderTime) { this.orderTime = orderTime; }
    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }
}
