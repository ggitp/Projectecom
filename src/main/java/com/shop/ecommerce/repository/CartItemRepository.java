package com.shop.ecommerce.repository;

import com.shop.ecommerce.model.CartItem;
import com.shop.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser(User user);
    List<CartItem> findByUserAndProduct(User user, com.shop.ecommerce.model.Product product);
} 