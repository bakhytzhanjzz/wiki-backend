package com.kz.wiki.service;

import com.kz.wiki.dto.request.*;
import com.kz.wiki.dto.response.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ClientService {
    ClientResponse create(CreateClientRequest request, String tenantId);
    ClientResponse update(Long id, CreateClientRequest request, String tenantId);
    ClientResponse findById(Long id, String tenantId);
    Page<ClientResponse> findAll(String tenantId, Pageable pageable, 
                                String search, List<Long> groupIds, List<Long> tagIds,
                                LocalDate birthdayFrom, LocalDate birthdayTo,
                                java.math.BigDecimal purchaseAmountFrom, java.math.BigDecimal purchaseAmountTo,
                                LocalDate lastPurchaseFrom, LocalDate lastPurchaseTo,
                                Integer noPurchaseDays,
                                LocalDate registrationFrom, LocalDate registrationTo,
                                List<Long> registrationStoreIds, String gender);
    Map<String, Object> getStatistics(String tenantId);
    void delete(Long id, String tenantId);
    Map<String, Integer> bulkUpdate(BulkUpdateClientsRequest request, String tenantId);
    Map<String, Integer> bulkAssignGroups(BulkAssignGroupsRequest request, String tenantId);
    Map<String, Integer> bulkRemoveGroups(BulkAssignGroupsRequest request, String tenantId);
    Map<String, Integer> bulkAssignTags(BulkAssignTagsRequest request, String tenantId);
    Map<String, Integer> bulkRemoveTags(BulkAssignTagsRequest request, String tenantId);
    Map<String, Integer> bulkDelete(BulkDeleteClientsRequest request, String tenantId);
    Map<String, Object> importClients(org.springframework.web.multipart.MultipartFile file, String tenantId);
    List<ClientResponse> searchForDebts(String search, String tenantId, Boolean hasDebt);
}


