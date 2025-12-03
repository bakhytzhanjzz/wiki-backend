package com.kz.wiki.repository;

import com.kz.wiki.entity.ImportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportItemRepository extends JpaRepository<ImportItem, Long> {
    List<ImportItem> findByImportEntityId(Long importId);
    void deleteByImportEntityId(Long importId);
}


