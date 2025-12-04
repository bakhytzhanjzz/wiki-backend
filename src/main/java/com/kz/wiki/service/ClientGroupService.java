package com.kz.wiki.service;

import com.kz.wiki.dto.request.CreateClientGroupRequest;
import com.kz.wiki.dto.response.ClientGroupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ClientGroupService {
    ClientGroupResponse create(CreateClientGroupRequest request, String tenantId);
    ClientGroupResponse update(Long id, CreateClientGroupRequest request, String tenantId);
    ClientGroupResponse findById(Long id, String tenantId);
    Page<ClientGroupResponse> findAll(String tenantId, Pageable pageable, String search, String status);
    Map<String, Object> getStatistics(String tenantId);
    void delete(Long id, String tenantId);
}


