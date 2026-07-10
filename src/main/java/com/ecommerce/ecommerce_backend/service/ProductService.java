package com.ecommerce.ecommerce_backend.service;

import com.ecommerce.ecommerce_backend.dto.product.ProductCreateRequest;
import com.ecommerce.ecommerce_backend.dto.product.ProductResponse;
import com.ecommerce.ecommerce_backend.dto.product.ProductUpdateRequest;
import com.ecommerce.ecommerce_backend.entity.Category;
import com.ecommerce.ecommerce_backend.entity.Product;
import com.ecommerce.ecommerce_backend.entity.Seller;
import com.ecommerce.ecommerce_backend.exception.ForbiddenException;
import com.ecommerce.ecommerce_backend.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce_backend.mapper.ProductMapper;
import com.ecommerce.ecommerce_backend.repository.CategoryRepository;
import com.ecommerce.ecommerce_backend.repository.ProductRepository;
import com.ecommerce.ecommerce_backend.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = findProductOrThrow(id);
        return productMapper.toResponse(product);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategory_Id(categoryId, pageable).map(productMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {
        return productRepository.findByTitleContainingIgnoreCase(keyword, pageable).map(productMapper::toResponse);
    }

    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request, Long userId) {
        Seller seller = sellerRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ForbiddenException("Only registered sellers can create products"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSeller(seller);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request, Long userId, boolean isAdmin) {
        Product product = findProductOrThrow(id);

        if (!isAdmin && !product.getSeller().getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to update this product");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        productMapper.updateEntityFromRequest(request, product);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        return productMapper.toResponse(saved);
    }

    @Transactional
    public void deleteProduct(Long id, Long userId) {
        Product product = findProductOrThrow(id);

        if (!product.getSeller().getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have permission to delete this product");
        }

        productRepository.delete(product);
    }

    private Product findProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }
}