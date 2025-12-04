package com.kz.wiki.repository;

import com.kz.wiki.entity.WriteOffItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WriteOffItemRepository extends JpaRepository<WriteOffItem, Long> {
    List<WriteOffItem> findByWriteOffId(Long writeOffId);
    void deleteByWriteOffId(Long writeOffId);
}




