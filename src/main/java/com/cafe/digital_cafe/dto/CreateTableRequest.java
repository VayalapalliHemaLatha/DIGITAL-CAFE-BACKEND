package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.TableStatus;

public class CreateTableRequest {

    private String tableNumber;
    private int capacity = 4;
    private TableStatus status;

    public CreateTableRequest() {
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
