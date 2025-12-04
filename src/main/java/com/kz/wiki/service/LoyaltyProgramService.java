package com.kz.wiki.service;

import com.kz.wiki.dto.request.CreateLoyaltyLevelRequest;
import com.kz.wiki.dto.request.CreateLoyaltyProgramRequest;
import com.kz.wiki.dto.response.LoyaltyProgramResponse;

public interface LoyaltyProgramService {
    LoyaltyProgramResponse get(String tenantId);
    LoyaltyProgramResponse update(CreateLoyaltyProgramRequest request, String tenantId);
    LoyaltyProgramResponse createLevel(CreateLoyaltyLevelRequest request, String tenantId);
    LoyaltyProgramResponse updateLevel(Long levelId, CreateLoyaltyLevelRequest request, String tenantId);
    LoyaltyProgramResponse deleteLevel(Long levelId, String tenantId);
}


