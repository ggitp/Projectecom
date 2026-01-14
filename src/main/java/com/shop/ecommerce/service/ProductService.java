package com.shop.ecommerce.service;

import com.shop.ecommerce.dto.ProductSuggestionDto;
import com.shop.ecommerce.dto.ProductUpsertRequest;
import com.shop.ecommerce.messaging.publisher.ProductEventPublisher;
import com.shop.ecommerce.messaging.dto.ProductEventType;
import com.shop.ecommerce.model.Discount;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/// Service containing business logic for products.
/// Fetches entities from repositories, applies rules (discounts),
/// and maps entities into DTOs.

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
            req.getViews(),
            req.getRating(),
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
    p.setRating(req.getRating());
    p.setViews(req.getViews());

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

  public ProductSuggestionDto toSuggestion(Product p) {
    float price = p.getPrice();
    Discount d = p.getDiscount();

    if (d == null || !d.isActiveNow(LocalDateTime.now())) {
      return new ProductSuggestionDto(
              p.getId(),
              p.getProductName(),
              p.getCategory(),
              price,
              price,
              null,
              p.getImageUrl()
      );
    }

    int percent = d.getPercent();
    float finalPrice = price * (100 - percent) / 100f;

    return new ProductSuggestionDto(
            p.getId(),
            p.getProductName(),
            p.getCategory(),
            price,
            finalPrice,
            percent,
            p.getImageUrl()
    );
  }
}