package com.cafe.digital_cafe.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_tables", uniqueConstraints = @UniqueConstraint(name = "uk_table_cafe_number", columnNames = {"cafe_id", "table_number"}))
public class RestaurantTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cafe_id", nullable = false)
    private Long cafeId;

    @Column(name = "table_number", nullable = false)
    private String tableNumber;

    @Column(nullable = false)
    private int capacity = 4;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TableStatus status = TableStatus.AVAILABLE;

    public RestaurantTable() {
    }

    public RestaurantTable(Long cafeId, String tableNumber, int capacity) {
        this.cafeId = cafeId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
    }

    public RestaurantTable(Long cafeId, String tableNumber, int capacity, TableStatus status) {
        this.cafeId = cafeId;
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status != null ? status : TableStatus.AVAILABLE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.status = status != null ? status : TableStatus.AVAILABLE;
    }

    public Long getCafeId() {
        return cafeId;
    }

    public void setCafeId(Long cafeId) {
        this.cafeId = cafeId;
    }
}
