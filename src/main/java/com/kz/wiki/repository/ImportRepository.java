package com.kz.wiki.repository;

import com.kz.wiki.entity.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImportRepository extends JpaRepository<Import, Long> {
    Optional<Import> findByIdAndTenantId(Long id, String tenantId);
    List<Import> findByTenantId(String tenantId);
    
    @Query("SELECT i FROM Import i WHERE i.tenantId = :tenantId AND " +
           "(LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(i.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<Import> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    List<Import> findByTenantIdAndStoreId(String tenantId, Long storeId);
    List<Import> findByTenantIdAndStatus(String tenantId, String status);
    
    @Query("SELECT i FROM Import i WHERE i.tenantId = :tenantId AND " +
           "DATE(i.createdAt) BETWEEN :startDate AND :endDate")
    List<Import> findByTenantIdAndDateRange(@Param("tenantId") String tenantId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);
}



