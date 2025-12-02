package com.kz.wiki.service;

import com.kz.wiki.entity.Customer;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer create(Customer customer, String tenantId);
    Customer update(Long id, Customer customer, String tenantId);
    Optional<Customer> findById(Long id, String tenantId);
    List<Customer> findAll(String tenantId);
    List<Customer> search(String searchTerm, String tenantId);
    List<Customer> quickSearch(String query, String tenantId, int limit);
    List<Customer> findWithDebt(String tenantId);
    List<Customer> findWithLoyaltyPoints(String tenantId);
    void delete(Long id, String tenantId);
    CustomerBalance getBalance(Long customerId, String tenantId);
    List<com.kz.wiki.entity.Sale> getPurchaseHistory(Long customerId, String tenantId, LocalDate startDate, LocalDate endDate);
    
    interface CustomerBalance {
        Integer getLoyaltyPoints();
        java.math.BigDecimal getDebtAmount();
        java.math.BigDecimal getTotalAccrued();
        java.math.BigDecimal getTotalSpent();
        java.math.BigDecimal getCurrentBalance();
    }
}

