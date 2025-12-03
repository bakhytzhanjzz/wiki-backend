package com.kz.wiki.repository;

import com.kz.wiki.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByIdAndTenantId(Long id, String tenantId);
    List<Transfer> findByTenantId(String tenantId);
    List<Transfer> findByTenantIdAndFromStoreId(String tenantId, Long fromStoreId);
    List<Transfer> findByTenantIdAndToStoreId(String tenantId, Long toStoreId);
    List<Transfer> findByTenantIdAndStatus(String tenantId, String status);
    
    @Query("SELECT t FROM Transfer t WHERE t.tenantId = :tenantId AND " +
           "(LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(t.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<Transfer> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT t FROM Transfer t WHERE t.tenantId = :tenantId AND " +
           "DATE(t.sentAt) BETWEEN :startDate AND :endDate")
    List<Transfer> findByTenantIdAndSentDateRange(@Param("tenantId") String tenantId,
                                                   @Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate);
}


