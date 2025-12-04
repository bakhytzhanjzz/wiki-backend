package com.kz.wiki.service.impl;

import com.kz.wiki.entity.SaleType;
import com.kz.wiki.repository.SaleRepository;
import com.kz.wiki.service.AnalyticsService;
import com.kz.wiki.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final SaleRepository saleRepository;

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getDailyRevenue(LocalDate date, String tenantId) {
        LocalDateTime start = DateUtil.getStartOfDay(date);
        LocalDateTime end = DateUtil.getEndOfDay(date);
        return saleRepository.sumTotalAmountByTenantIdAndDateRange(tenantId, start, end, SaleType.SALE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getDailySalesCount(LocalDate date, String tenantId) {
        LocalDateTime start = DateUtil.getStartOfDay(date);
        LocalDateTime end = DateUtil.getEndOfDay(date);
        return saleRepository.countByTenantIdAndDateRange(tenantId, start, end, SaleType.SALE);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getRevenueByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        LocalDateTime start = DateUtil.getStartOfDay(startDate);
        LocalDateTime end = DateUtil.getEndOfDay(endDate);
        return saleRepository.sumTotalAmountByTenantIdAndDateRange(tenantId, start, end, SaleType.SALE);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getSalesCountByDateRange(LocalDate startDate, LocalDate endDate, String tenantId) {
        LocalDateTime start = DateUtil.getStartOfDay(startDate);
        LocalDateTime end = DateUtil.getEndOfDay(endDate);
        return saleRepository.countByTenantIdAndDateRange(tenantId, start, end, SaleType.SALE);
    }
}








