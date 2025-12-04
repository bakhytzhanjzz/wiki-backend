package com.kz.wiki.repository;

import com.kz.wiki.entity.ClientTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientTagRepository extends JpaRepository<ClientTag, Long> {
    Optional<ClientTag> findByIdAndTenantId(Long id, String tenantId);
    List<ClientTag> findByTenantId(String tenantId);
    Page<ClientTag> findByTenantId(String tenantId, Pageable pageable);
    
    @Query("SELECT t FROM ClientTag t WHERE t.tenantId = :tenantId AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(t.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<ClientTag> searchByTenantId(@Param("tenantId") String tenantId,
                                     @Param("search") String search,
                                     Pageable pageable);
    
    @Query("SELECT t FROM ClientTag t WHERE t.tenantId = :tenantId AND " +
           "(:type IS NULL OR t.type = :type) AND " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(t.type) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "CAST(t.id AS string) LIKE CONCAT('%', :search, '%'))")
    Page<ClientTag> findByTenantIdAndFilters(@Param("tenantId") String tenantId,
                                              @Param("type") String type,
                                              @Param("status") String status,
                                              @Param("search") String search,
                                              Pageable pageable);
    
    boolean existsByIdAndTenantId(Long id, String tenantId);
}


