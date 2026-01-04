package com.shop.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductUpsertRequest {
  private String productName;
  private String description;
  private String category;
  private float price;
  private int quantity;
  private String imageUrl;
  private List<String> tags;
}