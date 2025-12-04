package com.kz.wiki.repository;

import com.kz.wiki.entity.CustomerGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerGroupRepository extends JpaRepository<CustomerGroup, Long> {
    List<CustomerGroup> findByCustomerIdAndTenantId(Long customerId, String tenantId);
    List<CustomerGroup> findByGroupIdAndTenantId(Long groupId, String tenantId);
    
    @Query("SELECT cg.groupId FROM CustomerGroup cg WHERE cg.customerId = :customerId AND cg.tenantId = :tenantId")
    List<Long> findGroupIdsByCustomerIdAndTenantId(@Param("customerId") Long customerId, @Param("tenantId") String tenantId);
    
    @Query("SELECT cg.customerId FROM CustomerGroup cg WHERE cg.groupId = :groupId AND cg.tenantId = :tenantId")
    List<Long> findCustomerIdsByGroupIdAndTenantId(@Param("groupId") Long groupId, @Param("tenantId") String tenantId);
    
    void deleteByCustomerIdAndGroupIdAndTenantId(Long customerId, Long groupId, String tenantId);
    void deleteByCustomerIdAndTenantId(Long customerId, String tenantId);
    void deleteByGroupIdAndTenantId(Long groupId, String tenantId);
}


