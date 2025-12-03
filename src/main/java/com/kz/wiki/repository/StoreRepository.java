package com.kz.wiki.repository;

import com.kz.wiki.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByIdAndTenantId(Long id, String tenantId);
    List<Store> findByTenantId(String tenantId);
    boolean existsByIdAndTenantId(Long id, String tenantId);
}


