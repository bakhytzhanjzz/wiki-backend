package com.kz.wiki.repository;

import com.kz.wiki.entity.Repricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepricingRepository extends JpaRepository<Repricing, Long> {
    Optional<Repricing> findByIdAndTenantId(Long id, String tenantId);
    List<Repricing> findByTenantId(String tenantId);
    List<Repricing> findByTenantIdAndStoreId(String tenantId, Long storeId);
    List<Repricing> findByTenantIdAndType(String tenantId, String type);
    
    @Query("SELECT r FROM Repricing r WHERE r.tenantId = :tenantId AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(r.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<Repricing> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT r FROM Repricing r WHERE r.tenantId = :tenantId AND " +
           "DATE(r.createdAt) = :date")
    List<Repricing> findByTenantIdAndDate(@Param("tenantId") String tenantId, @Param("date") LocalDate date);
}



