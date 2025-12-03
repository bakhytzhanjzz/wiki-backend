package com.kz.wiki.repository;

import com.kz.wiki.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByIdAndTenantId(Long id, String tenantId);
    List<Inventory> findByTenantId(String tenantId);
    List<Inventory> findByTenantIdAndStoreId(String tenantId, Long storeId);
    List<Inventory> findByTenantIdAndType(String tenantId, String type);
    List<Inventory> findByTenantIdAndStatus(String tenantId, String status);
    
    @Query("SELECT i FROM Inventory i WHERE i.tenantId = :tenantId AND " +
           "(LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(i.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<Inventory> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT i FROM Inventory i WHERE i.tenantId = :tenantId AND " +
           "DATE(i.createdAt) BETWEEN :startDate AND :endDate")
    List<Inventory> findByTenantIdAndDateRange(@Param("tenantId") String tenantId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}


