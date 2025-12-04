package com.kz.wiki.repository;

import com.kz.wiki.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByIdAndTenantId(Long id, String tenantId);
    List<Discount> findByTenantId(String tenantId);
    Optional<Discount> findByCodeAndTenantId(String code, String tenantId);
    
    @Query("SELECT d FROM Discount d WHERE d.tenantId = :tenantId AND " +
           "(LOWER(d.code) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(d.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Discount> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    List<Discount> findByTenantIdAndIsActive(String tenantId, Boolean isActive);
    List<Discount> findByTenantIdAndType(String tenantId, String type);
    
    @Query("SELECT d FROM Discount d WHERE d.tenantId = :tenantId AND " +
           "d.isActive = true AND " +
           "(d.startDate IS NULL OR d.startDate <= :now) AND " +
           "(d.endDate IS NULL OR d.endDate >= :now)")
    List<Discount> findActiveByTenantId(@Param("tenantId") String tenantId, @Param("now") LocalDateTime now);
}



