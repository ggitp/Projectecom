package com.shop.ecommerce.config;

import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.model.User;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create admin user if it doesn't exist
        if (!userRepository.existsByEmail("admin@ecommerce.com")) {
            User adminUser = new User();
            adminUser.setUserName("admin");
            adminUser.setEmail("admin@ecommerce.com");
            adminUser.setPasswordHash(passwordEncoder.encode("admin123"));
            adminUser.setCreatedAt(LocalDateTime.now());
            adminUser.setGuest(false);
            userRepository.save(adminUser);
            System.out.println("Admin user created: admin@ecommerce.com / admin123");
        }

        // Create regular user if it doesn't exist
        if (!userRepository.existsByEmail("user@ecommerce.com")) {
            User regularUser = new User();
            regularUser.setUserName("user");
            regularUser.setEmail("user@ecommerce.com");
            regularUser.setPasswordHash(passwordEncoder.encode("user123"));
            regularUser.setCreatedAt(LocalDateTime.now());
            regularUser.setGuest(false);
            userRepository.save(regularUser);
            System.out.println("Regular user created: user@ecommerce.com / user123");
        }

        // Create sample products if none exist
        if (productRepository.count() == 0) {
            createSampleProducts();
            System.out.println("Sample products created");
        }
    }

    private void createSampleProducts() {
        List<Product> products = Arrays.asList(
            new Product("iPhone 15 Pro", "Latest iPhone with advanced camera system", "Electronics", 999.99f, 50, "https://example.com/iphone15.jpg", LocalDateTime.now(), LocalDateTime.now(), Arrays.asList("smartphone", "apple", "camera")),
            new Product("MacBook Air M2", "Lightweight laptop with M2 chip", "Electronics", 1199.99f, 30, "https://example.com/macbook-air.jpg", LocalDateTime.now(), LocalDateTime.now(), Arrays.asList("laptop", "apple", "m2")),
            new Product("Nike Air Max", "Comfortable running shoes", "Sports", 129.99f, 100, "https://example.com/nike-airmax.jpg", LocalDateTime.now(), LocalDateTime.now(), Arrays.asList("shoes", "running", "nike")),
            new Product("Coffee Maker", "Automatic coffee machine", "Home & Kitchen", 89.99f, 75, "https://example.com/coffee-maker.jpg", LocalDateTime.now(), LocalDateTime.now(), Arrays.asList("coffee", "kitchen", "appliance")),
            new Product("Wireless Headphones", "Bluetooth noise-canceling headphones", "Electronics", 199.99f, 60, "https://example.com/headphones.jpg", LocalDateTime.now(), LocalDateTime.now(), Arrays.asList("audio", "bluetooth", "wireless"))
        );

        productRepository.saveAll(products);
    }
} 