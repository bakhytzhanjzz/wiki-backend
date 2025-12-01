package com.kz.wiki.repository;

import com.kz.wiki.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndTenantId(String email, String tenantId);
    boolean existsByEmailAndTenantId(String email, String tenantId);
    List<User> findByTenantId(String tenantId);
    Optional<User> findByIdAndTenantId(Long id, String tenantId);
}



