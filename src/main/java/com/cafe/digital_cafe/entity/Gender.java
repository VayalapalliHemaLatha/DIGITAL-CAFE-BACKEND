package com.cafe.digital_cafe.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Gender {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    private final String apiValue;

    Gender(String apiValue) {
        this.apiValue = apiValue;
    }

    @JsonValue
    public String getApiValue() {
        return apiValue;
    }

    @JsonCreator
    public static Gender fromApiValue(String value) {
        if (value == null || value.isBlank()) return null;
        return Arrays.stream(values())
                .filter(g -> g.apiValue.equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(null);
    }
}
