package com.shop.ecommerce.messaging.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductSyncDto {
  private int id;
  private String productName;
  private String description;
  private String category;
  private float price;
  private int quantity;
  private String imageUrl;
  private List<String> tags;
  private long views;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public ProductSyncDto() {}

  public ProductSyncDto(int id, String productName, String description, String category,
                        float price, int quantity, String imageUrl,
                        List<String> tags, long views, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.productName = productName;
    this.description = description;
    this.category = category;
    this.price = price;
    this.quantity = quantity;
    this.imageUrl = imageUrl;
    this.tags = tags;
    this.views = views;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static ProductSyncDto fromProduct(com.shop.ecommerce.model.Product p) {
    return new ProductSyncDto(
            p.getId(),
            p.getProductName(),
            p.getDescription(),
            p.getCategory(),
            p.getPrice(),
            p.getQuantity(),
            p.getImageUrl(),
            p.getTags(),
            p.getViews(),
            p.getCreatedAt(),
            p.getUpdatedAt()
    );
  }



}
