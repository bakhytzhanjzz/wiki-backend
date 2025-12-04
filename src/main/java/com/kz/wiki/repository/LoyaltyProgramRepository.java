package com.kz.wiki.repository;

import com.kz.wiki.entity.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    Optional<LoyaltyProgram> findByTenantId(String tenantId);
    Optional<LoyaltyProgram> findByIdAndTenantId(Long id, String tenantId);
    boolean existsByTenantId(String tenantId);
}


