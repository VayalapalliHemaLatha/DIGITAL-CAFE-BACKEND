package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.RoleType;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String name;
    private RoleType roleType;

    public AuthResponse() {
    }

    public AuthResponse(String token, Long id, String email, String name) {
        this(token, id, email, name, null);
    }

    public AuthResponse(String token, Long id, String email, String name, RoleType roleType) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.name = name;
        this.roleType = roleType;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }
}
