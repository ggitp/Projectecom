package com.shop.ecommerce.service;

import com.shop.ecommerce.dto.ProductSummaryDto;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

  private final ProductRepository productRepository;

  public RecommendationService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public List<ProductSummaryDto> getTrending(int limit) {
    List<Product> products = productRepository.findAllByOrderByViewsDesc(PageRequest.of(0, limit));

    return products.stream()
            .map(p -> new ProductSummaryDto(
                    p.getId(),
                    p.getProductName(),
                    p.getPrice(),
                    p.getImageUrl(),
                    p.getCategory(),
                    p.getSubCategory(),
                    p.getBrand(),
                    p.getViews(),
                    p.getRating()
            ))
            .toList();
  }
}