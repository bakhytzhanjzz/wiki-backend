package com.kz.wiki.repository;

import com.kz.wiki.entity.RepricingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepricingItemRepository extends JpaRepository<RepricingItem, Long> {
    List<RepricingItem> findByRepricingId(Long repricingId);
    void deleteByRepricingId(Long repricingId);
}




