package com.kz.wiki.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.dto.request.CreateClientGroupRequest;
import com.kz.wiki.dto.response.ClientGroupResponse;
import com.kz.wiki.entity.ClientGroup;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ClientGroupRepository;
import com.kz.wiki.repository.CustomerGroupRepository;
import com.kz.wiki.service.ClientGroupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientGroupServiceImpl implements ClientGroupService {

    private final ClientGroupRepository groupRepository;
    private final CustomerGroupRepository customerGroupRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_CLIENT_GROUP", entityType = "CLIENT_GROUP")
    public ClientGroupResponse create(CreateClientGroupRequest request, String tenantId) {
        ClientGroup group = new ClientGroup();
        group.setTenantId(tenantId);
        group.setName(request.getName());
        group.setDiscountPercent(request.getDiscountPercent());
        group.setDiscountApplication(request.getDiscountApplication());
        group.setStatus(request.getStatus());
        group.setDescription(request.getDescription());
        
        ClientGroup saved = groupRepository.save(group);
        return toResponse(saved);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_CLIENT_GROUP", entityType = "CLIENT_GROUP")
    public ClientGroupResponse update(Long id, CreateClientGroupRequest request, String tenantId) {
        ClientGroup group = groupRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientGroup", "id", id));
        
        group.setName(request.getName());
        group.setDiscountPercent(request.getDiscountPercent());
        group.setDiscountApplication(request.getDiscountApplication());
        group.setStatus(request.getStatus());
        group.setDescription(request.getDescription());
        group.setUpdatedAt(java.time.LocalDateTime.now());
        
        ClientGroup saved = groupRepository.save(group);
        return toResponse(saved);
    }

    @Override
    public ClientGroupResponse findById(Long id, String tenantId) {
        ClientGroup group = groupRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientGroup", "id", id));
        return toResponse(group);
    }

    @Override
    public Page<ClientGroupResponse> findAll(String tenantId, Pageable pageable, String search, String status) {
        Page<ClientGroup> groups;
        if (search != null && !search.isEmpty()) {
            groups = groupRepository.findByTenantIdAndStatus(tenantId, status, search, pageable);
        } else {
            groups = groupRepository.findByTenantId(tenantId, pageable);
        }
        
        List<ClientGroupResponse> responses = groups.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, groups.getTotalElements());
    }

    @Override
    public Map<String, Object> getStatistics(String tenantId) {
        long totalGroups = groupRepository.findByTenantId(tenantId).size();
        return Map.of("totalGroups", totalGroups);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_CLIENT_GROUP", entityType = "CLIENT_GROUP")
    public void delete(Long id, String tenantId) {
        ClientGroup group = groupRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientGroup", "id", id));
        
        // Remove all customer-group associations
        customerGroupRepository.deleteByGroupIdAndTenantId(id, tenantId);
        
        groupRepository.delete(group);
    }

    private ClientGroupResponse toResponse(ClientGroup group) {
        ClientGroupResponse response = new ClientGroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDiscountPercent(group.getDiscountPercent());
        response.setDiscountApplication(group.getDiscountApplication());
        response.setStatus(group.getStatus());
        response.setDescription(group.getDescription());
        response.setCreatedAt(group.getCreatedAt());
        response.setUpdatedAt(group.getUpdatedAt());
        
        // Count clients in this group
        long clientsCount = customerGroupRepository.findCustomerIdsByGroupIdAndTenantId(group.getId(), group.getTenantId()).size();
        response.setClientsCount((int) clientsCount);
        
        return response;
    }
}

