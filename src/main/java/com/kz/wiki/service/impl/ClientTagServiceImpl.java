package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.dto.request.CreateClientTagRequest;
import com.kz.wiki.dto.response.ClientTagResponse;
import com.kz.wiki.entity.ClientTag;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.ClientTagRepository;
import com.kz.wiki.repository.CustomerTagRepository;
import com.kz.wiki.service.ClientTagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientTagServiceImpl implements ClientTagService {

    private final ClientTagRepository tagRepository;
    private final CustomerTagRepository customerTagRepository;

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_CLIENT_TAG", entityType = "CLIENT_TAG")
    public ClientTagResponse create(CreateClientTagRequest request, String tenantId) {
        ClientTag tag = new ClientTag();
        tag.setTenantId(tenantId);
        tag.setName(request.getName());
        tag.setType(request.getType());
        tag.setStatus(request.getStatus());
        tag.setDescription(request.getDescription());
        
        ClientTag saved = tagRepository.save(tag);
        return toResponse(saved);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_CLIENT_TAG", entityType = "CLIENT_TAG")
    public ClientTagResponse update(Long id, CreateClientTagRequest request, String tenantId) {
        ClientTag tag = tagRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientTag", "id", id));
        
        tag.setName(request.getName());
        tag.setType(request.getType());
        tag.setStatus(request.getStatus());
        tag.setDescription(request.getDescription());
        tag.setUpdatedAt(java.time.LocalDateTime.now());
        
        ClientTag saved = tagRepository.save(tag);
        return toResponse(saved);
    }

    @Override
    public ClientTagResponse findById(Long id, String tenantId) {
        ClientTag tag = tagRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientTag", "id", id));
        return toResponse(tag);
    }

    @Override
    public Page<ClientTagResponse> findAll(String tenantId, Pageable pageable, String search, String type, String status) {
        Page<ClientTag> tags;
        if (search != null && !search.isEmpty() || type != null || status != null) {
            tags = tagRepository.findByTenantIdAndFilters(tenantId, type, status, search != null ? search : "", pageable);
        } else {
            tags = tagRepository.findByTenantId(tenantId, pageable);
        }
        
        List<ClientTagResponse> responses = tags.getContent().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, tags.getTotalElements());
    }

    @Override
    public Map<String, Object> getStatistics(String tenantId) {
        long totalTags = tagRepository.findByTenantId(tenantId).size();
        return Map.of("totalTags", totalTags);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_CLIENT_TAG", entityType = "CLIENT_TAG")
    public void delete(Long id, String tenantId) {
        ClientTag tag = tagRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("ClientTag", "id", id));
        
        // Remove all customer-tag associations
        customerTagRepository.deleteByTagIdAndTenantId(id, tenantId);
        
        tagRepository.delete(tag);
    }

    private ClientTagResponse toResponse(ClientTag tag) {
        ClientTagResponse response = new ClientTagResponse();
        response.setId(tag.getId());
        response.setName(tag.getName());
        response.setType(tag.getType());
        response.setStatus(tag.getStatus());
        response.setDescription(tag.getDescription());
        response.setCreatedAt(tag.getCreatedAt());
        response.setUpdatedAt(tag.getUpdatedAt());
        
        // Count clients with this tag
        long clientsCount = customerTagRepository.findCustomerIdsByTagIdAndTenantId(tag.getId(), tag.getTenantId()).size();
        response.setClientsCount((int) clientsCount);
        
        return response;
    }
}

