package com.shop.ecommerce.repository;

import com.shop.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(String category);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByPriceBetween(float minPrice, float maxPrice);
} 