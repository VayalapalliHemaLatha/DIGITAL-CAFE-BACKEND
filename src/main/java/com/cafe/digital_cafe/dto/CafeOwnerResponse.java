package com.cafe.digital_cafe.dto;

import com.cafe.digital_cafe.entity.RoleType;

/**
 * Cafe owner data for admin APIs. Includes active status.
 */
public class CafeOwnerResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private RoleType roleType;
    private boolean active;
    private Long cafeId;
    private String cafeName;

    public CafeOwnerResponse() {
    }

    public CafeOwnerResponse(Long id, String name, String email, String phone, String address, RoleType roleType, boolean active, Long cafeId, String cafeName) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.roleType = roleType;
        this.active = active;
        this.cafeId = cafeId;
        this.cafeName = cafeName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public RoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(RoleType roleType) {
        this.roleType = roleType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Long getCafeId() {
        return cafeId;
    }

    public void setCafeId(Long cafeId) {
        this.cafeId = cafeId;
    }

    public String getCafeName() {
        return cafeName;
    }

    public void setCafeName(String cafeName) {
        this.cafeName = cafeName;
    }
}
