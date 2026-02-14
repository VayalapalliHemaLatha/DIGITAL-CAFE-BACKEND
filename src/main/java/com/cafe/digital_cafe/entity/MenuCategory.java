package com.cafe.digital_cafe.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum MenuCategory {
    BEVERAGE("beverage"),
    FOOD("food"),
    DESSERT("dessert"),
    SNACK("snack");

    private final String apiValue;

    MenuCategory(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static MenuCategory fromApiValue(String value) {
        if (value == null || value.isBlank()) return null;
        return Arrays.stream(values())
                .filter(c -> c.apiValue.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}
