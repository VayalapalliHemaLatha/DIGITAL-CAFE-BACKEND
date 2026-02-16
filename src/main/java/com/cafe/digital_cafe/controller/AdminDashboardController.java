package com.cafe.digital_cafe.controller;

import com.cafe.digital_cafe.dto.*;
import com.cafe.digital_cafe.service.AdminDashboardService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    public AdminDashboardController(AdminDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Dashboard summary. Optional date filter: startDate, endDate (YYYY-MM-DD).
     * Without dates: all-time totals.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DashboardSummaryResponse response = dashboardService.getSummary(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Cafe locations for map display.
     */
    @GetMapping("/cafe-locations")
    public ResponseEntity<List<CafeLocationResponse>> getCafeLocations() {
        List<CafeLocationResponse> list = dashboardService.getCafeLocations();
        return ResponseEntity.ok(list);
    }

    /**
     * Daily sales and orders. Optional: startDate, endDate (default last 30 days).
     */
    @GetMapping("/daily-stats")
    public ResponseEntity<DailyStatsResponse.Wrapper> getDailyStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DailyStatsResponse.Wrapper response = dashboardService.getDailyStats(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    /**
     * Monthly sales and orders. Optional: year (default current), month (default all months).
     */
    @GetMapping("/monthly-stats")
    public ResponseEntity<MonthlyStatsResponse.Wrapper> getMonthlyStats(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        MonthlyStatsResponse.Wrapper response = dashboardService.getMonthlyStats(year, month);
        return ResponseEntity.ok(response);
    }
}
