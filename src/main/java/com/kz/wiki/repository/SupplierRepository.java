package com.kz.wiki.repository;

import com.kz.wiki.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByIdAndTenantId(Long id, String tenantId);
    List<Supplier> findByTenantId(String tenantId);
    boolean existsByIdAndTenantId(Long id, String tenantId);
    
    @Query("SELECT s FROM Supplier s WHERE s.tenantId = :tenantId AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Supplier> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
}

