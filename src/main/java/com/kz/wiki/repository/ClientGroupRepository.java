package com.kz.wiki.repository;

import com.kz.wiki.entity.ClientGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientGroupRepository extends JpaRepository<ClientGroup, Long> {
    Optional<ClientGroup> findByIdAndTenantId(Long id, String tenantId);
    List<ClientGroup> findByTenantId(String tenantId);
    Page<ClientGroup> findByTenantId(String tenantId, Pageable pageable);
    
    @Query("SELECT g FROM ClientGroup g WHERE g.tenantId = :tenantId AND " +
           "(LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(g.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<ClientGroup> searchByTenantId(@Param("tenantId") String tenantId, 
                                       @Param("search") String search, 
                                       Pageable pageable);
    
    @Query("SELECT g FROM ClientGroup g WHERE g.tenantId = :tenantId AND " +
           "(:status IS NULL OR g.status = :status) AND " +
           "(LOWER(g.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(g.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<ClientGroup> findByTenantIdAndStatus(@Param("tenantId") String tenantId,
                                               @Param("status") String status,
                                               @Param("search") String search,
                                               Pageable pageable);
    
    boolean existsByIdAndTenantId(Long id, String tenantId);
}


