package com.kz.wiki.service;

import com.kz.wiki.dto.request.*;
import com.kz.wiki.dto.response.ClientDebtResponse;
import com.kz.wiki.dto.response.DebtRepaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Map;

public interface ClientDebtService {
    ClientDebtResponse create(CreateClientDebtRequest request, String tenantId, Long userId);
    DebtRepaymentResponse repay(Long debtId, RepayDebtRequest request, String tenantId, Long userId);
    Map<String, Object> bulkRepay(BulkRepayDebtsRequest request, String tenantId, Long userId);
    Map<String, Object> sendSms(SendSmsToDebtorsRequest request, String tenantId);
    Page<ClientDebtResponse> findAll(String tenantId, Pageable pageable,
                                     String search, String status, Long storeId, String paymentType,
                                     java.math.BigDecimal repaymentAmountFrom, java.math.BigDecimal repaymentAmountTo,
                                     Long clientId, Long userId,
                                     LocalDate issueDateFrom, LocalDate issueDateTo);
    Page<DebtRepaymentResponse> findRepayments(String tenantId, Pageable pageable,
                                                String search, Long storeId, String paymentType,
                                                java.math.BigDecimal repaymentAmountFrom, java.math.BigDecimal repaymentAmountTo,
                                                Long clientId, Long userId,
                                                LocalDate repaymentDateFrom, LocalDate repaymentDateTo);
    Map<String, Object> getStatistics(String tenantId,
                                      LocalDate issueDateFrom, LocalDate issueDateTo,
                                      LocalDate repaymentDateFrom, LocalDate repaymentDateTo);
}


