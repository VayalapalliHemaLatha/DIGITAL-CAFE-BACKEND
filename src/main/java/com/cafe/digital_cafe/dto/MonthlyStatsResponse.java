package com.cafe.digital_cafe.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthlyStatsResponse {

    private int year;
    private int month;
    private String monthLabel; // e.g. "February 2025"
    private long orderCount;
    private BigDecimal sales;

    public MonthlyStatsResponse() {}
    public MonthlyStatsResponse(int year, int month, String monthLabel, long orderCount, BigDecimal sales) {
        this.year = year;
        this.month = month;
        this.monthLabel = monthLabel;
        this.orderCount = orderCount;
        this.sales = sales;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public String getMonthLabel() { return monthLabel; }
    public void setMonthLabel(String monthLabel) { this.monthLabel = monthLabel; }
    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    public BigDecimal getSales() { return sales; }
    public void setSales(BigDecimal sales) { this.sales = sales; }

    public static class Wrapper {
        private List<MonthlyStatsResponse> monthlyStats;
        private String period;

        public List<MonthlyStatsResponse> getMonthlyStats() { return monthlyStats; }
        public void setMonthlyStats(List<MonthlyStatsResponse> monthlyStats) { this.monthlyStats = monthlyStats; }
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
    }
}
