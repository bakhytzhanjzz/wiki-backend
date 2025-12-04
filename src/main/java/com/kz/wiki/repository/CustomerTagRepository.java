package com.kz.wiki.repository;

import com.kz.wiki.entity.CustomerTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerTagRepository extends JpaRepository<CustomerTag, Long> {
    List<CustomerTag> findByCustomerIdAndTenantId(Long customerId, String tenantId);
    List<CustomerTag> findByTagIdAndTenantId(Long tagId, String tenantId);
    
    @Query("SELECT ct.tagId FROM CustomerTag ct WHERE ct.customerId = :customerId AND ct.tenantId = :tenantId")
    List<Long> findTagIdsByCustomerIdAndTenantId(@Param("customerId") Long customerId, @Param("tenantId") String tenantId);
    
    @Query("SELECT ct.customerId FROM CustomerTag ct WHERE ct.tagId = :tagId AND ct.tenantId = :tenantId")
    List<Long> findCustomerIdsByTagIdAndTenantId(@Param("tagId") Long tagId, @Param("tenantId") String tenantId);
    
    void deleteByCustomerIdAndTagIdAndTenantId(Long customerId, Long tagId, String tenantId);
    void deleteByCustomerIdAndTenantId(Long customerId, String tenantId);
    void deleteByTagIdAndTenantId(Long tagId, String tenantId);
}

