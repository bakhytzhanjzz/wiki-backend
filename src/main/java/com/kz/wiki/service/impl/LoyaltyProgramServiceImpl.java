package com.kz.wiki.service.impl;

import com.kz.wiki.annotation.AuditLoggable;
import com.kz.wiki.dto.request.CreateLoyaltyLevelRequest;
import com.kz.wiki.dto.request.CreateLoyaltyProgramRequest;
import com.kz.wiki.dto.response.LoyaltyLevelResponse;
import com.kz.wiki.dto.response.LoyaltyProgramResponse;
import com.kz.wiki.entity.LoyaltyLevel;
import com.kz.wiki.entity.LoyaltyProgram;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.LoyaltyLevelRepository;
import com.kz.wiki.repository.LoyaltyProgramRepository;
import com.kz.wiki.service.LoyaltyProgramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoyaltyProgramServiceImpl implements LoyaltyProgramService {

    private final LoyaltyProgramRepository programRepository;
    private final LoyaltyLevelRepository levelRepository;

    @Override
    public LoyaltyProgramResponse get(String tenantId) {
        LoyaltyProgram program = programRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyProgram", "tenantId", tenantId));
        return toResponse(program);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_LOYALTY_PROGRAM", entityType = "LOYALTY_PROGRAM")
    public LoyaltyProgramResponse update(CreateLoyaltyProgramRequest request, String tenantId) {
        LoyaltyProgram program = programRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    LoyaltyProgram newProgram = new LoyaltyProgram();
                    newProgram.setTenantId(tenantId);
                    return newProgram;
                });
        
        program.setType(request.getType());
        program.setName(request.getName());
        program.setUpdatedAt(java.time.LocalDateTime.now());
        
        LoyaltyProgram saved = programRepository.save(program);
        return toResponse(saved);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "CREATE_LOYALTY_LEVEL", entityType = "LOYALTY_LEVEL")
    public LoyaltyProgramResponse createLevel(CreateLoyaltyLevelRequest request, String tenantId) {
        LoyaltyProgram program = programRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    LoyaltyProgram newProgram = new LoyaltyProgram();
                    newProgram.setTenantId(tenantId);
                    newProgram.setType("discount");
                    newProgram.setName("Default Program");
                    return programRepository.save(newProgram);
                });
        
        LoyaltyLevel level = new LoyaltyLevel();
        level.setTenantId(tenantId);
        level.setLoyaltyProgramId(program.getId());
        level.setName(request.getName());
        level.setPurchaseAmount(request.getPurchaseAmount());
        level.setDiscount(request.getDiscount());
        level.setOrder(request.getOrder());
        
        levelRepository.save(level);
        
        // Refresh to get updated levels
        Long programId = program.getId();
        LoyaltyProgram refreshedProgram = programRepository.findByIdAndTenantId(programId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyProgram", "id", programId));
        
        return toResponse(refreshedProgram);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "UPDATE_LOYALTY_LEVEL", entityType = "LOYALTY_LEVEL")
    public LoyaltyProgramResponse updateLevel(Long levelId, CreateLoyaltyLevelRequest request, String tenantId) {
        LoyaltyLevel level = levelRepository.findByIdAndTenantId(levelId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyLevel", "id", levelId));
        
        level.setName(request.getName());
        level.setPurchaseAmount(request.getPurchaseAmount());
        level.setDiscount(request.getDiscount());
        level.setOrder(request.getOrder());
        
        levelRepository.save(level);
        
        LoyaltyProgram program = programRepository.findByIdAndTenantId(level.getLoyaltyProgramId(), tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyProgram", "id", level.getLoyaltyProgramId()));
        
        return toResponse(program);
    }

    @Override
    @Transactional
    @AuditLoggable(action = "DELETE_LOYALTY_LEVEL", entityType = "LOYALTY_LEVEL")
    public LoyaltyProgramResponse deleteLevel(Long levelId, String tenantId) {
        LoyaltyLevel level = levelRepository.findByIdAndTenantId(levelId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyLevel", "id", levelId));
        
        Long programId = level.getLoyaltyProgramId();
        levelRepository.delete(level);
        
        LoyaltyProgram program = programRepository.findByIdAndTenantId(programId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("LoyaltyProgram", "id", programId));
        
        return toResponse(program);
    }

    private LoyaltyProgramResponse toResponse(LoyaltyProgram program) {
        LoyaltyProgramResponse response = new LoyaltyProgramResponse();
        response.setId(program.getId());
        response.setType(program.getType());
        response.setName(program.getName());
        response.setCreatedAt(program.getCreatedAt());
        response.setUpdatedAt(program.getUpdatedAt());
        
        List<LoyaltyLevel> levels = levelRepository.findByLoyaltyProgramIdOrderByOrderAsc(program.getId());
        List<LoyaltyLevelResponse> levelResponses = levels.stream()
                .map(level -> toLevelResponse(level))
                .collect(Collectors.toList());
        response.setLevels(levelResponses);
        
        return response;
    }

    private LoyaltyLevelResponse toLevelResponse(LoyaltyLevel level) {
        LoyaltyLevelResponse response = new LoyaltyLevelResponse();
        response.setId(level.getId());
        response.setName(level.getName());
        response.setPurchaseAmount(level.getPurchaseAmount());
        response.setDiscount(level.getDiscount());
        response.setOrder(level.getOrder());
        return response;
    }
}

