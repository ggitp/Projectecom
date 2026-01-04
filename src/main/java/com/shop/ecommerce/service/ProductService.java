package com.shop.ecommerce.service;

import com.shop.ecommerce.dto.ProductUpsertRequest;
import com.shop.ecommerce.messaging.publisher.ProductEventPublisher;
import com.shop.ecommerce.messaging.dto.ProductEventType;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductEventPublisher productEventPublisher;

  public ProductService(ProductRepository productRepository, ProductEventPublisher productEventPublisher) {
    this.productRepository = productRepository;
    this.productEventPublisher = productEventPublisher;
  }

  public Product create(ProductUpsertRequest req) {
    LocalDateTime now = LocalDateTime.now();

    Product p = new Product(
            req.getProductName(),
            req.getDescription(),
            req.getCategory(),
            req.getPrice(),
            req.getQuantity(),
            req.getImageUrl(),
            now, now,
            req.getTags()
    );
    p.setCreatedAt(now);
    p.setUpdatedAt(now);

    Product saved = productRepository.save(p);
    productEventPublisher.publish(ProductEventType.CREATED, saved);
    return saved;
  }

  public Product update(int id, ProductUpsertRequest req) {
    Product p = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));

    p.setProductName(req.getProductName());
    p.setDescription(req.getDescription());
    p.setCategory(req.getCategory());
    p.setPrice(req.getPrice());
    p.setQuantity(req.getQuantity());
    p.setImageUrl(req.getImageUrl());
    p.setTags(req.getTags());
    p.setUpdatedAt(LocalDateTime.now());

    Product saved = productRepository.save(p);
    productEventPublisher.publish(ProductEventType.UPDATED, saved);
    return saved;
  }

  public void delete(int id) {
    Product p = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));

    productEventPublisher.publish(ProductEventType.DELETED, p);
    productRepository.deleteById(id);
  }

  public Product getById(int id) {
    return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found: " + id));
  }
}