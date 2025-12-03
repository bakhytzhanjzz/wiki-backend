package com.kz.wiki.repository;

import com.kz.wiki.entity.TransferItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferItemRepository extends JpaRepository<TransferItem, Long> {
    List<TransferItem> findByTransferId(Long transferId);
    void deleteByTransferId(Long transferId);
}


