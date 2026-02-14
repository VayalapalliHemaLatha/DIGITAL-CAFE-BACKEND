package com.cafe.digital_cafe.dto;

import java.util.List;

public class CafeDetailResponse {

    private Long id;
    private String name;
    private String address;
    private String phone;
    private int tableCount;
    private List<TableResponse> tables;
    private List<MenuItemResponse> menu;

    public CafeDetailResponse() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getTableCount() { return tableCount; }
    public void setTableCount(int tableCount) { this.tableCount = tableCount; }
    public List<TableResponse> getTables() { return tables; }
    public void setTables(List<TableResponse> tables) { this.tables = tables; }
    public List<MenuItemResponse> getMenu() { return menu; }
    public void setMenu(List<MenuItemResponse> menu) { this.menu = menu; }
}
