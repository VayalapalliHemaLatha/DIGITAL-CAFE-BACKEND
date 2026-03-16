package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.DailyStatsResponse;
import com.cafe.digital_cafe.dto.DashboardSummaryResponse;
import com.cafe.digital_cafe.dto.MonthlyStatsResponse;
import com.cafe.digital_cafe.service.CafeOwnerDashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/cafeowners/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000", "http://localhost:7000", "http://127.0.0.1:7000"})
public class CafeOwnerDashboardController {

    private final CafeOwnerDashboardService dashboardService;

    public CafeOwnerDashboardController(CafeOwnerDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Dashboard summary. Optional date filter: startDate, endDate (YYYY-MM-DD).
     * Without dates: all-time totals for this cafe owner.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DashboardSummaryResponse response = dashboardService.getSummary(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Daily sales and orders for this cafe owner. Optional: startDate, endDate (default last 30 days).
     */
    @GetMapping("/daily-stats")
    public ResponseEntity<DailyStatsResponse.Wrapper> getDailyStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DailyStatsResponse.Wrapper response = dashboardService.getDailyStats(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Monthly sales and orders for this cafe owner. Optional: year (default current), month (default all months).
     */
    @GetMapping("/monthly-stats")
    public ResponseEntity<MonthlyStatsResponse.Wrapper> getMonthlyStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        MonthlyStatsResponse.Wrapper response = dashboardService.getMonthlyStats(year, month);
        return ResponseEntity.ok(response);
    }
}
