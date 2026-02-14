package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.TableStatus;

public class UpdateTableStatusRequest {

    private TableStatus status;

    public UpdateTableStatusRequest() {
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }
}
