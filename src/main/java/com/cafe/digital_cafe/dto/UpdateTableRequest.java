package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.TableStatus;

/**
 * All fields optional - only non-null fields are updated.
 */
public class UpdateTableRequest {

    private String tableNumber;
    private Integer capacity;
    private TableStatus status;

    public UpdateTableRequest() {
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }
}
