package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.TableStatus;

public class TableResponse {

    private Long id;
    private String tableNumber;
    private int capacity;
    private TableStatus status;

    public TableResponse() {
    }

    public TableResponse(Long id, String tableNumber, int capacity, TableStatus status) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }
}
