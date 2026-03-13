package com.cafe.digital_cafe.dto;

public class SimpleOrderRequest {
    
    private Long customerId;
    private String tableNumber;
    
    public SimpleOrderRequest() {}
    
    public SimpleOrderRequest(Long customerId, String tableNumber) {
        this.customerId = customerId;
        this.tableNumber = tableNumber;
    }
    
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    
    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }
}
