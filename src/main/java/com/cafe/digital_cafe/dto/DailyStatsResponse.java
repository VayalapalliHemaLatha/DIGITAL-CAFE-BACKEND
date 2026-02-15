package com.cafe.digital_cafe.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DailyStatsResponse {

    private LocalDate date;
    private long orderCount;
    private BigDecimal sales;

    public DailyStatsResponse() {}
    public DailyStatsResponse(LocalDate date, long orderCount, BigDecimal sales) {
        this.date = date;
        this.orderCount = orderCount;
        this.sales = sales;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public long getOrderCount() { return orderCount; }
    public void setOrderCount(long orderCount) { this.orderCount = orderCount; }
    public BigDecimal getSales() { return sales; }
    public void setSales(BigDecimal sales) { this.sales = sales; }

    public static class Wrapper {
        private List<DailyStatsResponse> dailyStats;
        private String period; // e.g. "2025-02-01 to 2025-02-15"

        public List<DailyStatsResponse> getDailyStats() { return dailyStats; }
        public void setDailyStats(List<DailyStatsResponse> dailyStats) { this.dailyStats = dailyStats; }
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
    }
}
