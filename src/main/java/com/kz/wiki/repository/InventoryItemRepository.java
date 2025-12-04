package com.kz.wiki.repository;

import com.kz.wiki.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByInventoryId(Long inventoryId);
    void deleteByInventoryId(Long inventoryId);
}



