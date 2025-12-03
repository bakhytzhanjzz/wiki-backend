package com.kz.wiki.repository;

import com.kz.wiki.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByTenantId(String tenantId);
    boolean existsByTenantId(String tenantId);
}







