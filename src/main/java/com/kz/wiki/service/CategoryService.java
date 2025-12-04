package com.kz.wiki.service;

import com.kz.wiki.entity.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category create(Category category, String tenantId);
    Category update(Long id, Category category, String tenantId);
    Optional<Category> findById(Long id, String tenantId);
    List<Category> findAll(String tenantId);
    void delete(Long id, String tenantId);
    boolean existsById(Long id, String tenantId);
}






