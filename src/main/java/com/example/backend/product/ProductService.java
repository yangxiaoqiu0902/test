package com.example.backend.product;

import com.example.backend.common.dto.PageResponse;
import com.example.backend.common.exception.ResourceNotFoundException;
import com.example.backend.product.dto.ProductRequest;
import com.example.backend.product.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stock()
        );
        return ProductResponse.from(repository.save(product));
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(find(id));
    }

    public PageResponse<ProductResponse> list(Pageable pageable) {
        Page<Product> page = repository.findAll(pageable);
        return PageResponse.of(page, ProductResponse::from);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = find(id);
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        return ProductResponse.from(repository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Product find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product " + id + " not found"));
    }
}
