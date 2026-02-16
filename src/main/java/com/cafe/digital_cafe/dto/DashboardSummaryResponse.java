package com.cafe.digital_cafe.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Dashboard summary - total customers, sales, orders, status breakdown.
 */
public class DashboardSummaryResponse {

    private long totalCustomers;
    private long totalCafes;
    private long totalOrders;
    private BigDecimal totalSales;
    private Map<String, Long> ordersByStatus;

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    public long getTotalCafes() { return totalCafes; }
    public void setTotalCafes(long totalCafes) { this.totalCafes = totalCafes; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public BigDecimal getTotalSales() { return totalSales; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public Map<String, Long> getOrdersByStatus() { return ordersByStatus; }
    public void setOrdersByStatus(Map<String, Long> ordersByStatus) { this.ordersByStatus = ordersByStatus; }
}
