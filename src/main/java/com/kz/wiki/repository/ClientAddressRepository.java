package com.kz.wiki.repository;

import com.kz.wiki.entity.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientAddressRepository extends JpaRepository<ClientAddress, Long> {
    Optional<ClientAddress> findByIdAndTenantId(Long id, String tenantId);
    List<ClientAddress> findByClientIdAndTenantId(Long clientId, String tenantId);
    void deleteByClientIdAndTenantId(Long clientId, String tenantId);
}


