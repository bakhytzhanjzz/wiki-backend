package com.kz.wiki.repository;

import com.kz.wiki.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByIdAndTenantId(Long id, String tenantId);
    List<Customer> findByTenantId(String tenantId);
    Optional<Customer> findByPhoneAndTenantId(String phone, String tenantId);
    Optional<Customer> findByEmailAndTenantId(String email, String tenantId);
    
    @Query("SELECT c FROM Customer c WHERE c.tenantId = :tenantId AND " +
           "(LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.middleName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Customer> searchByTenantId(@Param("tenantId") String tenantId, @Param("searchTerm") String searchTerm);
    
    List<Customer> findByTenantIdAndDebtAmountGreaterThan(String tenantId, java.math.BigDecimal zero);
    List<Customer> findByTenantIdAndLoyaltyPointsGreaterThan(String tenantId, Integer zero);
    boolean existsByIdAndTenantId(Long id, String tenantId);
}

