package com.kz.wiki.repository;

import com.kz.wiki.entity.ClientRelative;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRelativeRepository extends JpaRepository<ClientRelative, Long> {
    Optional<ClientRelative> findByIdAndTenantId(Long id, String tenantId);
    List<ClientRelative> findByClientIdAndTenantId(Long clientId, String tenantId);
    void deleteByClientIdAndTenantId(Long clientId, String tenantId);
}

