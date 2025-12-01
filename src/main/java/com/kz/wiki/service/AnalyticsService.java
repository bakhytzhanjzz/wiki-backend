package com.kz.wiki.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AnalyticsService {
    BigDecimal getDailyRevenue(LocalDate date, String tenantId);
    Long getDailySalesCount(LocalDate date, String tenantId);
    BigDecimal getRevenueByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
    Long getSalesCountByDateRange(LocalDate startDate, LocalDate endDate, String tenantId);
}





