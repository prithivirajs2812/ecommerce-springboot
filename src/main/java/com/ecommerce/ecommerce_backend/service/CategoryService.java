package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.category.CategoryRequest;
import com.ecommerce.ecommerce_backend.dto.category.CategoryResponse;
import com.ecommerce.ecommerce_backend.entity.Category;
import com.ecommerce.ecommerce_backend.exception.BadRequestException;
import com.ecommerce.ecommerce_backend.exception.ConflictException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.CategoryMapper;
import com.ecommerce.ecommerce_backend.repository.CategoryRepository;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toResponseWithCount)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryOrThrow(id);
        return toResponseWithCount(category);
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new BadRequestException("A category with this name already exists");
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);
        return toResponseWithCount(saved);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);

        categoryRepository.findByName(request.getName()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new BadRequestException("A category with this name already exists");
            }
        });

        categoryMapper.updateEntityFromRequest(request, category);
        Category saved = categoryRepository.save(category);
        return toResponseWithCount(saved);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);

        long productCount = productRepository.findByCategory_Id(id, org.springframework.data.domain.Pageable.unpaged())
                .getTotalElements();

        if (productCount > 0) {
            throw new ConflictException(
                    "Cannot delete category with " + productCount + " existing product(s). Reassign or remove them first."
            );
        }

        categoryRepository.delete(category);
    }

    private CategoryResponse toResponseWithCount(Category category) {
        CategoryResponse response = categoryMapper.toResponse(category);
        response.setProductCount(productRepository.findByCategory_Id(category.getId(), org.springframework.data.domain.Pageable.unpaged()).getTotalElements());
        return response;
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }
}