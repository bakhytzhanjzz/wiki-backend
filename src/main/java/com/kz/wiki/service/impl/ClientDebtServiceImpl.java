package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.dto.request.*;
import com.kz.wiki.dto.response.ClientDebtResponse;
import com.kz.wiki.dto.response.DebtRepaymentResponse;
import com.kz.wiki.entity.Customer;
import com.kz.wiki.entity.CustomerDebt;
import com.kz.wiki.entity.DebtPayment;
import com.kz.wiki.entity.Store;
import com.kz.wiki.entity.User;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.CustomerDebtRepository;
import com.kz.wiki.repository.CustomerRepository;
import com.kz.wiki.repository.DebtPaymentRepository;
import com.kz.wiki.repository.StoreRepository;
import com.kz.wiki.repository.UserRepository;
import com.kz.wiki.service.ClientDebtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientDebtServiceImpl implements ClientDebtService {

    private final CustomerDebtRepository debtRepository;
    private final DebtPaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_CLIENT_DEBT", entityType = "CLIENT_DEBT")
    public ClientDebtResponse create(CreateClientDebtRequest request, String tenantId, Long userId) {
        Customer customer = customerRepository.findByIdAndTenantId(request.getClientId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getClientId()));
        
        CustomerDebt debt = new CustomerDebt();
        debt.setTenantId(tenantId);
        debt.setCustomerId(request.getClientId());
        debt.setAmount(request.getAmount());
        debt.setRemainingAmount(request.getAmount());
        debt.setPaidAmount(BigDecimal.ZERO);
        debt.setStatus("unpaid");
        debt.setIssueDate(LocalDateTime.now());
        debt.setDueDate(request.getDueDate());
        debt.setStoreId(request.getStoreId());
        debt.setUserId(userId);
        debt.setPaymentType(request.getPaymentType());
        debt.setNotes(request.getNotes());
        
        CustomerDebt saved = debtRepository.save(debt);
        return toDebtResponse(saved);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "REPAY_DEBT", entityType = "DEBT_PAYMENT")
    public DebtRepaymentResponse repay(Long debtId, RepayDebtRequest request, String tenantId, Long userId) {
        CustomerDebt debt = debtRepository.findByIdAndTenantId(debtId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerDebt", "id", debtId));
        
        if (debt.getRemainingAmount().compareTo(request.getAmount()) < 0) {
            throw new BadRequestException("Repayment amount exceeds remaining debt amount");
        }
        
        DebtPayment payment = new DebtPayment();
        payment.setTenantId(tenantId);
        payment.setCustomerId(debt.getCustomerId());
        payment.setDebtId(debtId);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setUserId(userId);
        payment.setNotes(request.getNotes());
        payment.setCreatedBy(userId);
        
        paymentRepository.save(payment);
        
        // Update debt
        BigDecimal newPaidAmount = debt.getPaidAmount().add(request.getAmount());
        BigDecimal newRemainingAmount = debt.getAmount().subtract(newPaidAmount);
        
        debt.setPaidAmount(newPaidAmount);
        debt.setRemainingAmount(newRemainingAmount);
        
        if (newRemainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            debt.setStatus("paid");
        } else {
            debt.setStatus("partial");
        }
        
        debt.setUpdatedAt(LocalDateTime.now());
        debtRepository.save(debt);
        
        // Update customer debt amount
        updateCustomerDebtAmount(debt.getCustomerId(), tenantId);
        
        return toRepaymentResponse(payment);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "BULK_REPAY_DEBTS", entityType = "DEBT_PAYMENT")
    public Map<String, Object> bulkRepay(BulkRepayDebtsRequest request, String tenantId, Long userId) {
        int repaid = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Long debtId : request.getDebtIds()) {
            CustomerDebt debt = debtRepository.findByIdAndTenantId(debtId, tenantId)
                    .orElse(null);
            
            if (debt != null && debt.getRemainingAmount().compareTo(request.getAmount()) >= 0) {
                RepayDebtRequest repayRequest = new RepayDebtRequest();
                repayRequest.setAmount(request.getAmount());
                repayRequest.setPaymentMethod(request.getPaymentMethod());
                repayRequest.setNotes(request.getNotes());
                
                repay(debtId, repayRequest, tenantId, userId);
                repaid++;
                totalAmount = totalAmount.add(request.getAmount());
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("repaid", repaid);
        result.put("totalAmount", totalAmount);
        return result;
    }

    @Override
    @AuditLoggable(action = "SEND_SMS_TO_DEBTORS", entityType = "CLIENT_DEBT")
    public Map<String, Object> sendSms(SendSmsToDebtorsRequest request, String tenantId) {
        // TODO: Implement SMS sending logic
        // For now, just return success
        int sent = 0;
        int failed = 0;
        
        for (Long debtId : request.getDebtIds()) {
            CustomerDebt debt = debtRepository.findByIdAndTenantId(debtId, tenantId).orElse(null);
            if (debt != null) {
                Customer customer = customerRepository.findByIdAndTenantId(debt.getCustomerId(), tenantId).orElse(null);
                if (customer != null && customer.getPhone() != null) {
                    // SMS sending logic would go here
                    sent++;
                } else {
                    failed++;
                }
            } else {
                failed++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("sent", sent);
        result.put("failed", failed);
        return result;
    }

    @Override
    public Page<ClientDebtResponse> findAll(String tenantId, Pageable pageable,
                                            String search, String status, Long storeId, String paymentType,
                                            BigDecimal repaymentAmountFrom, BigDecimal repaymentAmountTo,
                                            Long clientId, Long userId,
                                            LocalDate issueDateFrom, LocalDate issueDateTo) {
        LocalDateTime issueDateFromDateTime = issueDateFrom != null ? issueDateFrom.atStartOfDay() : null;
        LocalDateTime issueDateToDateTime = issueDateTo != null ? issueDateTo.atTime(23, 59, 59) : null;
        
        Page<CustomerDebt> debts = debtRepository.findByFilters(
                tenantId, search, status, storeId, paymentType,
                repaymentAmountFrom, repaymentAmountTo, clientId, userId,
                issueDateFromDateTime, issueDateToDateTime, pageable);
        
        List<ClientDebtResponse> responses = debts.getContent().stream()
                .map(this::toDebtResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, debts.getTotalElements());
    }

    @Override
    public Page<DebtRepaymentResponse> findRepayments(String tenantId, Pageable pageable,
                                                      String search, Long storeId, String paymentType,
                                                      BigDecimal repaymentAmountFrom, BigDecimal repaymentAmountTo,
                                                      Long clientId, Long userId,
                                                      LocalDate repaymentDateFrom, LocalDate repaymentDateTo) {
        LocalDateTime repaymentDateFromDateTime = repaymentDateFrom != null ? repaymentDateFrom.atStartOfDay() : null;
        LocalDateTime repaymentDateToDateTime = repaymentDateTo != null ? repaymentDateTo.atTime(23, 59, 59) : null;
        
        Page<DebtPayment> payments = paymentRepository.findByFilters(
                tenantId, search, storeId, paymentType,
                repaymentAmountFrom, repaymentAmountTo, clientId, userId,
                repaymentDateFromDateTime, repaymentDateToDateTime, pageable);
        
        List<DebtRepaymentResponse> responses = payments.getContent().stream()
                .map(this::toRepaymentResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, payments.getTotalElements());
    }

    @Override
    public Map<String, Object> getStatistics(String tenantId,
                                             LocalDate issueDateFrom, LocalDate issueDateTo,
                                             LocalDate repaymentDateFrom, LocalDate repaymentDateTo) {
        List<CustomerDebt> allDebts = debtRepository.findAll().stream()
                .filter(d -> d.getTenantId().equals(tenantId))
                .collect(Collectors.toList());
        
        BigDecimal totalDebtAmount = allDebts.stream()
                .map(CustomerDebt::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPaidAmount = allDebts.stream()
                .map(CustomerDebt::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal remainingDebt = allDebts.stream()
                .map(CustomerDebt::getRemainingAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long totalDebtors = allDebts.stream()
                .map(CustomerDebt::getCustomerId)
                .distinct()
                .count();
        
        long paidDebts = allDebts.stream()
                .filter(d -> "paid".equals(d.getStatus()))
                .count();
        
        long unpaidDebts = allDebts.stream()
                .filter(d -> "unpaid".equals(d.getStatus()))
                .count();
        
        long overdueDebts = allDebts.stream()
                .filter(d -> d.getIsOverdue() != null && d.getIsOverdue())
                .count();
        
        long totalRepayments = paymentRepository.findAll().stream()
                .filter(p -> p.getTenantId().equals(tenantId))
                .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalDebtAmount", totalDebtAmount);
        stats.put("totalPaidAmount", totalPaidAmount);
        stats.put("systemRepayments", totalPaidAmount); // Same as totalPaidAmount for now
        stats.put("remainingDebt", remainingDebt);
        stats.put("totalDebtors", totalDebtors);
        stats.put("paidDebts", paidDebts);
        stats.put("unpaidDebts", unpaidDebts);
        stats.put("overdueDebts", overdueDebts);
        stats.put("totalRepayments", totalRepayments);
        
        return stats;
    }

    private ClientDebtResponse toDebtResponse(CustomerDebt debt) {
        ClientDebtResponse response = new ClientDebtResponse();
        response.setId(debt.getId());
        response.setClientId(debt.getCustomerId());
        response.setAmount(debt.getAmount());
        response.setPaidAmount(debt.getPaidAmount());
        response.setRemainingAmount(debt.getRemainingAmount());
        response.setStatus(debt.getStatus());
        response.setIssueDate(debt.getIssueDate());
        response.setDueDate(debt.getDueDate());
        response.setStoreId(debt.getStoreId());
        response.setUserId(debt.getUserId());
        response.setPaymentType(debt.getPaymentType());
        response.setNotes(debt.getNotes());
        response.setCreatedAt(debt.getCreatedAt());
        response.setUpdatedAt(debt.getUpdatedAt());
        
        // Load customer info
        Customer customer = customerRepository.findByIdAndTenantId(debt.getCustomerId(), debt.getTenantId()).orElse(null);
        if (customer != null) {
            response.setClientName(customer.getFirstName() + " " + customer.getLastName());
            response.setClientPhone(customer.getPhone());
        }
        
        // Load store info
        if (debt.getStoreId() != null) {
            Store store = storeRepository.findByIdAndTenantId(debt.getStoreId(), debt.getTenantId()).orElse(null);
            if (store != null) {
                response.setStoreName(store.getName());
            }
        }
        
        // Load user info
        if (debt.getUserId() != null) {
            User user = userRepository.findByIdAndTenantId(debt.getUserId(), debt.getTenantId()).orElse(null);
            if (user != null) {
                response.setUserName(user.getEmail());
            }
        }
        
        return response;
    }

    private DebtRepaymentResponse toRepaymentResponse(DebtPayment payment) {
        DebtRepaymentResponse response = new DebtRepaymentResponse();
        response.setId(payment.getId());
        response.setDebtId(payment.getDebtId());
        response.setAmount(payment.getAmount());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setPaymentDate(payment.getPaymentDate());
        response.setUserId(payment.getUserId());
        response.setNotes(payment.getNotes());
        response.setCreatedAt(payment.getCreatedAt());
        
        // Load user info
        if (payment.getUserId() != null) {
            User user = userRepository.findByIdAndTenantId(payment.getUserId(), payment.getTenantId()).orElse(null);
            if (user != null) {
                response.setUserName(user.getEmail());
            }
        }
        
        return response;
    }

    private void updateCustomerDebtAmount(Long customerId, String tenantId) {
        Customer customer = customerRepository.findByIdAndTenantId(customerId, tenantId).orElse(null);
        if (customer != null) {
            List<CustomerDebt> debts = debtRepository.findByCustomerIdAndTenantId(customerId, tenantId);
            BigDecimal totalDebt = debts.stream()
                    .map(CustomerDebt::getRemainingAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            customer.setDebtAmount(totalDebt);
            customerRepository.save(customer);
        }
    }
}

