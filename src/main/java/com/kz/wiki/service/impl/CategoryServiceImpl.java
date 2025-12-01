package com.kz.wiki.service.impl;

import com.kz.wiki.entity.Category;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.ResourceNotFoundException;
import com.kz.wiki.repository.CategoryRepository;
import com.kz.wiki.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category create(Category category, String tenantId) {
        // Check if category with same name already exists for this tenant
        List<Category> existing = categoryRepository.findByTenantId(tenantId);
        boolean nameExists = existing.stream()
                .anyMatch(c -> c.getName().equalsIgnoreCase(category.getName()));
        
        if (nameExists) {
            throw new BadRequestException("Category with name '" + category.getName() + "' already exists");
        }

        category.setTenantId(tenantId);
        Category saved = categoryRepository.save(category);
        log.info("Category created: {} (ID: {}) for tenant: {}", saved.getName(), saved.getId(), tenantId);
        return saved;
    }

    @Override
    @Transactional
    public Category update(Long id, Category category, String tenantId) {
        Category existing = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        // Check if new name conflicts with another category
        List<Category> allCategories = categoryRepository.findByTenantId(tenantId);
        boolean nameExists = allCategories.stream()
                .anyMatch(c -> !c.getId().equals(id) && c.getName().equalsIgnoreCase(category.getName()));
        
        if (nameExists) {
            throw new BadRequestException("Category with name '" + category.getName() + "' already exists");
        }

        existing.setName(category.getName());
        Category updated = categoryRepository.save(existing);
        log.info("Category updated: {} (ID: {}) for tenant: {}", updated.getName(), updated.getId(), tenantId);
        return updated;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id, String tenantId) {
        return categoryRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll(String tenantId) {
        return categoryRepository.findByTenantId(tenantId);
    }

    @Override
    @Transactional
    public void delete(Long id, String tenantId) {
        Category category = categoryRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        categoryRepository.delete(category);
        log.info("Category deleted: {} (ID: {}) for tenant: {}", category.getName(), category.getId(), tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id, String tenantId) {
        return categoryRepository.existsByIdAndTenantId(id, tenantId);
    }
}

