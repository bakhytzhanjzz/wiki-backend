package com.kz.wiki.repository;

import com.kz.wiki.entity.WriteOff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WriteOffRepository extends JpaRepository<WriteOff, Long> {
    Optional<WriteOff> findByIdAndTenantId(Long id, String tenantId);
    List<WriteOff> findByTenantId(String tenantId);
    List<WriteOff> findByTenantIdAndStoreId(String tenantId, Long storeId);
    List<WriteOff> findByTenantIdAndType(String tenantId, String type);
    
    @Query("SELECT w FROM WriteOff w WHERE w.tenantId = :tenantId AND " +
           "(LOWER(w.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(w.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<WriteOff> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT w FROM WriteOff w WHERE w.tenantId = :tenantId AND " +
           "DATE(w.createdAt) BETWEEN :startDate AND :endDate")
    List<WriteOff> findByTenantIdAndDateRange(@Param("tenantId") String tenantId,
                                               @Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);
}




