package com.kz.wiki.repository;

import com.kz.wiki.entity.LoyaltyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyLevelRepository extends JpaRepository<LoyaltyLevel, Long> {
    Optional<LoyaltyLevel> findByIdAndTenantId(Long id, String tenantId);
    List<LoyaltyLevel> findByLoyaltyProgramIdAndTenantId(Long loyaltyProgramId, String tenantId);
    List<LoyaltyLevel> findByLoyaltyProgramIdAndTenantIdOrderByOrderAsc(Long loyaltyProgramId, String tenantId);
    boolean existsByIdAndTenantId(Long id, String tenantId);
}


