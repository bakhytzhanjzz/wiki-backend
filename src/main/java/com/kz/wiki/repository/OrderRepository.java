package com.kz.wiki.repository;

import com.kz.wiki.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByIdAndTenantId(Long id, String tenantId);
    List<Order> findByTenantId(String tenantId);
    List<Order> findByTenantIdAndStoreId(String tenantId, Long storeId);
    List<Order> findByTenantIdAndStatus(String tenantId, String status);
    
    @Query("SELECT o FROM Order o WHERE o.tenantId = :tenantId AND " +
           "(LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(o.id AS string) LIKE CONCAT('%', :searchTerm, '%'))")
    List<Order> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    @Query("SELECT o FROM Order o WHERE o.tenantId = :tenantId AND " +
           "DATE(o.createdAt) BETWEEN :startDate AND :endDate")
    List<Order> findByTenantIdAndDateRange(@Param("tenantId") String tenantId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}


