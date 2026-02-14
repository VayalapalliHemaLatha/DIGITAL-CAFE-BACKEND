package com.cafe.digital_cafe.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum OrderStatus {
    PLACED("placed"),
    PREPARING("preparing"),
    READY("ready"),
    SERVED("served");

    private final String apiValue;

    OrderStatus(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static OrderStatus fromApiValue(String value) {
        if (value == null || value.isBlank()) return null;
        return Arrays.stream(values())
                .filter(s -> s.apiValue.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}
