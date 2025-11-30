package com.kz.wiki.repository;

import com.kz.wiki.entity.StockTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, Long> {
    List<StockTransaction> findByTenantId(String tenantId);
    List<StockTransaction> findByTenantIdAndProductId(String tenantId, Long productId);
    
    @Query("SELECT COALESCE(SUM(st.quantityChange), 0) FROM StockTransaction st " +
           "WHERE st.tenantId = :tenantId AND st.product.id = :productId")
    Integer getTotalQuantityChangeByProduct(
            @Param("tenantId") String tenantId,
            @Param("productId") Long productId
    );
}

