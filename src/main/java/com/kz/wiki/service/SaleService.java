package com.kz.wiki.service;

import com.kz.wiki.entity.Sale;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SaleService {
    Sale createSale(Sale sale, String tenantId, Long userId);
    Sale updateSale(Long id, Sale sale, String tenantId);
    Sale cancelSale(Long id, String reason, String tenantId, Long userId);
    Sale createReturn(Long saleId, CreateReturnRequest request, String tenantId, Long userId);
    Sale createExchange(Long saleId, CreateExchangeRequest request, String tenantId, Long userId);
    Sale saveDraft(Sale sale, String tenantId, Long userId);
    Sale saveDeferred(Sale sale, String tenantId, Long userId);
    Sale completeDraft(Long id, Sale sale, String tenantId, Long userId);
    Sale completeDeferred(Long id, Sale sale, String tenantId, Long userId);
    Optional<Sale> findById(Long id, String tenantId);
    List<Sale> findAll(String tenantId);
    List<Sale> findWithFilters(SaleFilters filters, String tenantId);
    List<Sale> findDrafts(String tenantId);
    List<Sale> findDeferred(String tenantId);
    List<Sale> findReturns(String tenantId);
    List<Sale> findExchanges(String tenantId);
    SaleStatistics getStatistics(SaleStatisticsFilters filters, String tenantId);
    SaleStatisticsByDate getStatisticsByDate(LocalDate startDate, LocalDate endDate, String groupBy, String tenantId);
    String getNextTransactionNumber(Long storeId, String tenantId);
    
    interface CreateReturnRequest {
        List<ReturnItemRequest> getItems();
        String getRefundMethod();
        String getNote();
        
        interface ReturnItemRequest {
            Long getSaleItemId();
            Integer getQuantity();
            String getReason();
        }
    }
    
    interface CreateExchangeRequest {
        List<ReturnItemRequest> getReturnItems();
        List<NewItemRequest> getNewItems();
        BigDecimal getDifferenceAmount();
        String getPaymentMethod();
        String getNote();
        
        interface ReturnItemRequest {
            Long getSaleItemId();
            Integer getQuantity();
            String getReason();
        }
        
        interface NewItemRequest {
            Long getProductId();
            Integer getQuantity();
            BigDecimal getPrice();
        }
    }
    
    interface SaleFilters {
        String getSearch();
        Long getStoreId();
        String getPaymentMethod();
        Long getSellerId();
        Long getCustomerId();
        BigDecimal getMinAmount();
        BigDecimal getMaxAmount();
        LocalDate getStartDate();
        LocalDate getEndDate();
        String getStatus();
        String getType();
        Integer getPage();
        Integer getLimit();
    }
    
    interface SaleStatisticsFilters {
        Long getStoreId();
        Long getSellerId();
        LocalDate getStartDate();
        LocalDate getEndDate();
        String getGroupBy();
    }
    
    interface SaleStatistics {
        Long getTotalTransactions();
        BigDecimal getTotalAmount();
        Long getTotalProducts();
        Long getTotalServices();
        Long getTotalKits();
        Long getTotalCertificates();
        Long getTotalReturns();
        BigDecimal getTotalReturnsAmount();
        Long getTotalExchanges();
        BigDecimal getTotalExchangesAmount();
        Long getTotalGiftCardsUsed();
        BigDecimal getTotalGiftCardsAmount();
        Integer getTotalLoyaltyPointsEarned();
        Integer getTotalLoyaltyPointsUsed();
        BigDecimal getTotalDebtPayments();
        CustomerBalance getCustomerBalance();
        List<PaymentMethodStats> getByPaymentMethod();
        List<TypeStats> getByType();
    }
    
    interface CustomerBalance {
        BigDecimal getTotalAccrued();
        BigDecimal getTotalSpent();
        BigDecimal getCurrentBalance();
    }
    
    interface PaymentMethodStats {
        String getMethod();
        Long getCount();
        BigDecimal getAmount();
    }
    
    interface TypeStats {
        String getType();
        Long getCount();
        BigDecimal getAmount();
    }
    
    interface SaleStatisticsByDate {
        List<DateStats> getData();
    }
    
    interface DateStats {
        String getDate();
        Long getTransactions();
        BigDecimal getAmount();
        Long getProducts();
        Long getServices();
    }
}





