package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/info")
    public Map<String, Object> getAdminInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // Get admin user
        User adminUser = userRepository.findByEmail("admin@ecommerce.com").orElse(null);
        if (adminUser != null) {
            Map<String, Object> admin = new HashMap<>();
            admin.put("id", adminUser.getId());
            admin.put("username", adminUser.getUserName());
            admin.put("email", adminUser.getEmail());
            admin.put("isGuest", adminUser.isGuest());
            admin.put("createdAt", adminUser.getCreatedAt());
            info.put("adminUser", admin);
        }
        
        // Get product count
        long productCount = productRepository.count();
        info.put("totalProducts", productCount);
        
        // Get sample products
        List<Product> products = productRepository.findAll();
        info.put("products", products);
        
        return info;
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
} 