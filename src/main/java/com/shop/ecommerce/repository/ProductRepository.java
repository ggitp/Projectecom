package com.shop.ecommerce.repository;

import com.shop.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;

/// Repository responsible for fetching Product entities from the database.
/// Used only by the service layer.

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(String category);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByPriceBetween(float minPrice, float maxPrice);
    List<Product> findAllByOrderByViewsDesc(Pageable pageable);
    List<Product> findAllByOrderByRatingDesc(Pageable pageable);
} 