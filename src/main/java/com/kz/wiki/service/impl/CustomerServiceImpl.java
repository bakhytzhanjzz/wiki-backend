package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.entity.Customer;
import com.kz.wiki.entity.CustomerDebt;
import com.kz.wiki.entity.DebtPayment;
import com.kz.wiki.entity.Sale;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.CustomerDebtRepository;
import com.kz.wiki.repository.CustomerRepository;
import com.kz.wiki.repository.DebtPaymentRepository;
import com.kz.wiki.repository.SaleRepository;
import com.kz.wiki.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;
    private final CustomerDebtRepository debtRepository;
    private final DebtPaymentRepository paymentRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_CUSTOMER", entityType = "CUSTOMER")
    public Customer create(Customer customer, String tenantId) {
        // Check phone uniqueness
        if (customer.getPhone() != null && customerRepository.findByPhoneAndTenantId(customer.getPhone(), tenantId).isPresent()) {
            throw new BadRequestException("Customer with phone " + customer.getPhone() + " already exists");
        }
        
        // Check email uniqueness
        if (customer.getEmail() != null && customerRepository.findByEmailAndTenantId(customer.getEmail(), tenantId).isPresent()) {
            throw new BadRequestException("Customer with email " + customer.getEmail() + " already exists");
        }

        customer.setTenantId(tenantId);
        Customer saved = customerRepository.save(customer);
        String customerName = (saved.getFirstName() != null ? saved.getFirstName() : "") + 
                             (saved.getLastName() != null ? " " + saved.getLastName() : "");
        log.info("Customer created: {} (ID: {}) for tenant: {}", customerName.trim(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_CUSTOMER", entityType = "CUSTOMER")
    public Customer update(Long id, Customer customer, String tenantId) {
        Customer existing = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Check phone uniqueness if changed
        if (customer.getPhone() != null && !customer.getPhone().equals(existing.getPhone()) &&
            customerRepository.findByPhoneAndTenantId(customer.getPhone(), tenantId).isPresent()) {
            throw new BadRequestException("Customer with phone " + customer.getPhone() + " already exists");
        }

        existing.setFirstName(customer.getFirstName());
        existing.setLastName(customer.getLastName());
        existing.setMiddleName(customer.getMiddleName());
        existing.setPhone(customer.getPhone());
        existing.setEmail(customer.getEmail());
        existing.setNotes(customer.getNotes());

        Customer updated = customerRepository.save(existing);
        String customerName = (updated.getFirstName() != null ? updated.getFirstName() : "") + 
                             (updated.getLastName() != null ? " " + updated.getLastName() : "");
        log.info("Customer updated: {} (ID: {}) for tenant: {}", customerName.trim(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findById(Long id, String tenantId) {
        return customerRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAll(String tenantId) {
        return customerRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> search(String searchTerm, String tenantId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findAll(tenantId);
        }
        return customerRepository.searchByTenantId(tenantId, searchTerm.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> quickSearch(String query, String tenantId, int limit) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        List<Customer> results = customerRepository.searchByTenantId(tenantId, query.trim());
        return results.stream().limit(Math.min(limit, 20)).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findWithDebt(String tenantId) {
        return customerRepository.findByTenantIdAndDebtAmountGreaterThan(tenantId, BigDecimal.ZERO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findWithLoyaltyPoints(String tenantId) {
        return customerRepository.findByTenantIdAndLoyaltyPointsGreaterThan(tenantId, 0);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_CUSTOMER", entityType = "CUSTOMER")
    public void delete(Long id, String tenantId) {
        Customer customer = customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        
        customerRepository.delete(customer);
        String customerName = (customer.getFirstName() != null ? customer.getFirstName() : "") + 
                             (customer.getLastName() != null ? " " + customer.getLastName() : "");
        log.info("Customer deleted: {} (ID: {}) for tenant: {}", customerName.trim(), customer.getId(), tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerBalance getBalance(Long customerId, String tenantId) {
        Customer customer = customerRepository.findByIdAndTenantId(customerId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        BigDecimal totalPayments = paymentRepository.getTotalPaymentsByCustomerId(customerId, tenantId);
        if (totalPayments == null) totalPayments = BigDecimal.ZERO;

        List<CustomerDebt> debts = debtRepository.findByCustomerIdAndTenantId(customerId, tenantId);
        BigDecimal totalDebt = debts.stream()
                .map(CustomerDebt::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAccrued = customer.getTotalPurchases() != null ? customer.getTotalPurchases() : BigDecimal.ZERO;
        BigDecimal totalSpent = totalPayments;
        BigDecimal currentBalance = totalAccrued.subtract(totalSpent);

        final BigDecimal finalTotalPayments = totalPayments;
        return new CustomerBalance() {
            @Override
            public Integer getLoyaltyPoints() {
                return customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0;
            }

            @Override
            public BigDecimal getDebtAmount() {
                return customer.getDebtAmount() != null ? customer.getDebtAmount() : BigDecimal.ZERO;
            }

            @Override
            public BigDecimal getTotalAccrued() {
                return totalAccrued;
            }

            @Override
            public BigDecimal getTotalSpent() {
                return finalTotalPayments;
            }

            @Override
            public BigDecimal getCurrentBalance() {
                return currentBalance;
            }
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<Sale> getPurchaseHistory(Long customerId, String tenantId, LocalDate startDate, LocalDate endDate) {
        if (!customerRepository.existsByIdAndTenantId(customerId, tenantId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }

        List<Sale> sales = saleRepository.findByTenantIdAndCustomerId(tenantId, customerId);
        
        if (startDate != null && endDate != null) {
            sales = sales.stream()
                    .filter(sale -> {
                        LocalDate saleDate = sale.getSaleTime().toLocalDate();
                        return !saleDate.isBefore(startDate) && !saleDate.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
        }
        
        return sales;
    }
}

