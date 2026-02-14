package com.cafe.digital_cafe.dto;

import java.util.List;

/**
 * Response for GET tables - includes list and counts.
 */
public class TablesSummaryResponse {

    private List<TableResponse> tables;
    private long availableCount;
    private long bookedCount;
    private long totalCount;

    public TablesSummaryResponse() {
    }

    public TablesSummaryResponse(List<TableResponse> tables, long availableCount, long bookedCount) {
        this.tables = tables;
        this.availableCount = availableCount;
        this.bookedCount = bookedCount;
        this.totalCount = availableCount + bookedCount;
    }

    public List<TableResponse> getTables() {
        return tables;
    }

    public void setTables(List<TableResponse> tables) {
        this.tables = tables;
    }

    public long getAvailableCount() {
        return availableCount;
    }

    public void setAvailableCount(long availableCount) {
        this.availableCount = availableCount;
    }

    public long getBookedCount() {
        return bookedCount;
    }

    public void setBookedCount(long bookedCount) {
        this.bookedCount = bookedCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
}
