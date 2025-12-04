package com.kz.wiki.service;

import com.kz.wiki.dto.request.CreateClientTagRequest;
import com.kz.wiki.dto.response.ClientTagResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface ClientTagService {
    ClientTagResponse create(CreateClientTagRequest request, String tenantId);
    ClientTagResponse update(Long id, CreateClientTagRequest request, String tenantId);
    ClientTagResponse findById(Long id, String tenantId);
    Page<ClientTagResponse> findAll(String tenantId, Pageable pageable, String search, String type, String status);
    Map<String, Object> getStatistics(String tenantId);
    void delete(Long id, String tenantId);
}

