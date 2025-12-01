package com.kz.wiki.repository;

import com.kz.wiki.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByIdAndTenantId(Long id, String tenantId);
    Optional<Product> findBySkuAndTenantId(String sku, String tenantId);
    boolean existsBySkuAndTenantId(String sku, String tenantId);
    List<Product> findByTenantId(String tenantId);
    List<Product> findByTenantIdAndCategoryId(String tenantId, Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.tenantId = :tenantId AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.sku) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.barcode) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Product> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
}
