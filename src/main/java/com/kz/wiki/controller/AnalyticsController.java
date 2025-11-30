package com.kz.wiki.controller;

import com.kz.wiki.dto.response.ApiResponse;
import com.kz.wiki.service.AnalyticsService;
import com.kz.wiki.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/daily/revenue")
    public ResponseEntity<ApiResponse<BigDecimal>> getDailyRevenue(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LocalDate targetDate = date != null ? date : LocalDate.now();
        BigDecimal revenue = analyticsService.getDailyRevenue(targetDate, tenantId);
        return ResponseEntity.ok(ApiResponse.success(revenue));
    }

    @GetMapping("/daily/sales-count")
    public ResponseEntity<ApiResponse<Long>> getDailySalesCount(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LocalDate targetDate = date != null ? date : LocalDate.now();
        Long count = analyticsService.getDailySalesCount(targetDate, tenantId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<BigDecimal>> getRevenueByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        BigDecimal revenue = analyticsService.getRevenueByDateRange(startDate, endDate, tenantId);
        return ResponseEntity.ok(ApiResponse.success(revenue));
    }

    @GetMapping("/sales-count")
    public ResponseEntity<ApiResponse<Long>> getSalesCountByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        Long count = analyticsService.getSalesCountByDateRange(startDate, endDate, tenantId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        String tenantId = SecurityUtil.getCurrentTenantId();
        LocalDate targetDate = date != null ? date : LocalDate.now();
        
        BigDecimal revenue = analyticsService.getDailyRevenue(targetDate, tenantId);
        Long salesCount = analyticsService.getDailySalesCount(targetDate, tenantId);
        
        DashboardResponse dashboard = DashboardResponse.builder()
                .date(targetDate)
                .dailyRevenue(revenue)
                .dailySalesCount(salesCount)
                .build();
        
        return ResponseEntity.ok(ApiResponse.success(dashboard));
    }

    @lombok.Data
    @lombok.Builder
    public static class DashboardResponse {
        private LocalDate date;
        private BigDecimal dailyRevenue;
        private Long dailySalesCount;
    }
}

