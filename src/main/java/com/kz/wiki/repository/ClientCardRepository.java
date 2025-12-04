package com.kz.wiki.repository;

import com.kz.wiki.entity.ClientCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientCardRepository extends JpaRepository<ClientCard, Long> {
    Optional<ClientCard> findByIdAndTenantId(Long id, String tenantId);
    List<ClientCard> findByClientIdAndTenantId(Long clientId, String tenantId);
    void deleteByClientIdAndTenantId(Long clientId, String tenantId);
}

