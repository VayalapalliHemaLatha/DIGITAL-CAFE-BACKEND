package com.cafe.digital_cafe.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

/**
 * User role types: admin, cafeowner, chef, waiter, customer.
 * Hierarchy: ADMIN > CAFE_OWNER > CHEF/WAITER > CUSTOMER
 */
public enum RoleType {
    ADMIN("admin"),
    CAFE_OWNER("cafeowner"),
    CHEF("chef"),
    WAITER("waiter"),
    CUSTOMER("customer");

    private final String apiValue;

    RoleType(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static RoleType fromApiValue(String value) {
        if (value == null || value.isBlank()) return null;
        return Arrays.stream(values())
                .filter(r -> r.apiValue.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}
