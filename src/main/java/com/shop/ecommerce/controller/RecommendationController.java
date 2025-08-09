package com.shop.ecommerce.controller;

import com.shop.ecommerce.model.User;
import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.repository.UserRepository;
import com.shop.ecommerce.repository.ProductRepository;
import com.shop.ecommerce.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * REST Controller for Recommendation System
 * 
 * Provides endpoints for:
 * - Getting recommendations for users (guest and registered)
 * - Getting similar products for product detail pages
 * - Getting trending products
 * - Getting category-based recommendations
 */
@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartItemRepository cartItemRepository;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final String ML_SERVICE_URL = "http://localhost:5000";
    
    /**
     * Load data to Python ML service
     */
    private void loadDataToMLService() {
        try {
            List<User> users = userRepository.findAll();
            List<Product> products = productRepository.findAll();
            List<com.shop.ecommerce.model.CartItem> cartItems = cartItemRepository.findAll();
            
            // Convert to JSON-serializable format
            List<Map<String, Object>> usersData = new ArrayList<>();
            for (User user : users) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("userName", user.getUserName());
                userData.put("email", user.getEmail());
                userData.put("isGuest", user.isGuest());
                userData.put("searchHistory", user.getSearchHistory());
                userData.put("purchaseHistory", user.getPurchaseHistory());
                usersData.add(userData);
            }
            
            List<Map<String, Object>> productsData = new ArrayList<>();
            for (Product product : products) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("id", product.getId());
                productData.put("productName", product.getProductName());
                productData.put("description", product.getDescription());
                productData.put("category", product.getCategory());
                productData.put("price", product.getPrice());
                productData.put("quantity", product.getQuantity());
                productData.put("views", product.getViews());
                productData.put("tags", product.getTags());
                productsData.add(productData);
            }
            
            List<Map<String, Object>> interactionsData = new ArrayList<>();
            for (com.shop.ecommerce.model.CartItem cartItem : cartItems) {
                Map<String, Object> interactionData = new HashMap<>();
                interactionData.put("user_id", cartItem.getUser().getId());
                interactionData.put("product_id", cartItem.getProduct().getId());
                interactionData.put("weight", 2); // Cart addition weight
                interactionData.put("timestamp", cartItem.getAddedAt().toString());
                interactionsData.add(interactionData);
            }
            
            Map<String, Object> dataPayload = new HashMap<>();
            dataPayload.put("products", productsData);
            dataPayload.put("users", usersData);
            dataPayload.put("interactions", interactionsData);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(dataPayload, headers);
            
            restTemplate.postForEntity(ML_SERVICE_URL + "/data/load", request, Map.class);
            
        } catch (Exception e) {
            System.err.println("Error loading data to ML service: " + e.getMessage());
        }
    }
    
    /**
     * Get recommendations for a user
     * 
     * @param userId The user ID
     * @param limit Maximum number of recommendations (default: 10)
     * @return List of recommended products
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Product>> getUserRecommendations(
            @PathVariable int userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        User user = userOpt.get();
        
        // Load data to ML service first
        loadDataToMLService();
        
        // Call Python ML service
        String url = ML_SERVICE_URL + "/recommendations/personalized/" + userId + "?limit=" + limit;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    
    /**
     * Get similar products for a product detail page
     * 
     * @param productId The product ID
     * @param limit Maximum number of similar products (default: 5)
     * @return List of similar products
     */
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<Product>> getSimilarProducts(
            @PathVariable int productId,
            @RequestParam(defaultValue = "5") int limit) {
        
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        // Load data to ML service first
        loadDataToMLService();
        
        // Call Python ML service
        String url = ML_SERVICE_URL + "/recommendations/similar/" + productId + "?limit=" + limit;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    
    /**
     * Get trending products
     * 
     * @param limit Maximum number of trending products (default: 10)
     * @return List of trending products
     */
    @GetMapping("/trending")
    public ResponseEntity<List<Product>> getTrendingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        
        // Load data to ML service first
        loadDataToMLService();
        
        // Call Python ML service
        String url = ML_SERVICE_URL + "/recommendations/trending?limit=" + limit;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    
    /**
     * Get recommendations by category
     * 
     * @param category The category name
     * @param limit Maximum number of recommendations (default: 10)
     * @return List of products in the category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getRecommendationsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "10") int limit) {
        
        // Load data to ML service first
        loadDataToMLService();
        
        // Call Python ML service
        String url = ML_SERVICE_URL + "/recommendations/category/" + category + "?limit=" + limit;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    
    /**
     * Record a product view (for analytics)
     * 
     * @param productId The product ID that was viewed
     * @return Success response
     */
    @PostMapping("/view/{productId}")
    public ResponseEntity<Map<String, String>> recordProductView(@PathVariable int productId) {
        // Call Python ML service to record interaction
        Map<String, Object> interactionData = new HashMap<>();
        interactionData.put("user_id", 1); // Default user for guest views
        interactionData.put("product_id", productId);
        interactionData.put("interaction_type", "view");
        interactionData.put("weight", 1);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(interactionData, headers);
        
        try {
            restTemplate.postForEntity(ML_SERVICE_URL + "/interactions/record", request, Map.class);
            Map<String, String> successMap = new HashMap<>();
            successMap.put("message", "Product view recorded successfully");
            return ResponseEntity.ok(successMap);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to record view");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
    
    /**
     * Record a search term
     * 
     * @param userId The user ID
     * @param searchData The search data containing the search term
     * @return Success response
     */
    @PostMapping("/search/{userId}")
    public ResponseEntity<Map<String, String>> recordSearch(
            @PathVariable int userId,
            @RequestBody Map<String, String> searchData) {
        
        String searchTerm = searchData.get("searchTerm");
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Search term is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
        }
        
        // Call Python ML service to record interaction
        Map<String, Object> interactionData = new HashMap<>();
        interactionData.put("user_id", userId);
        interactionData.put("product_id", 1); // Default product for search
        interactionData.put("interaction_type", "search");
        interactionData.put("weight", 1);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(interactionData, headers);
        
        try {
            restTemplate.postForEntity(ML_SERVICE_URL + "/interactions/record", request, Map.class);
            Map<String, String> successMap = new HashMap<>();
            successMap.put("message", "Search recorded successfully");
            return ResponseEntity.ok(successMap);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to record search");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
    
    /**
     * Record a purchase
     * 
     * @param userId The user ID
     * @param purchaseData The purchase data containing the product ID
     * @return Success response
     */
    @PostMapping("/purchase/{userId}")
    public ResponseEntity<Map<String, String>> recordPurchase(
            @PathVariable int userId,
            @RequestBody Map<String, Integer> purchaseData) {
        
        Integer productId = purchaseData.get("productId");
        if (productId == null) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Product ID is required");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
        }
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
        }
        
        // Call Python ML service to record interaction
        Map<String, Object> interactionData = new HashMap<>();
        interactionData.put("user_id", userId);
        interactionData.put("product_id", productId);
        interactionData.put("interaction_type", "purchase");
        interactionData.put("weight", 3); // Higher weight for purchases
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(interactionData, headers);
        
        try {
            restTemplate.postForEntity(ML_SERVICE_URL + "/interactions/record", request, Map.class);
            Map<String, String> successMap = new HashMap<>();
            successMap.put("message", "Purchase recorded successfully");
            return ResponseEntity.ok(successMap);
        } catch (Exception e) {
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Failed to record purchase");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMap);
        }
    }
    
    /**
     * Get guest recommendations (popular products)
     * 
     * @param limit Maximum number of recommendations (default: 10)
     * @return List of popular products
     */
    @GetMapping("/guest")
    public ResponseEntity<List<Product>> getGuestRecommendations(
            @RequestParam(defaultValue = "10") int limit) {
        
        // Load data to ML service first
        loadDataToMLService();
        
        // Call Python ML service
        String url = ML_SERVICE_URL + "/recommendations/guest?limit=" + limit;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Fallback: return popular products from database when ML service is unavailable
            List<Product> popularProducts = productRepository.findAll();
            if (popularProducts.size() > limit) {
                popularProducts = popularProducts.subList(0, limit);
            }
            return ResponseEntity.ok(popularProducts);
        }
    }
    
    /**
     * Get personalized recommendations for registered users
     * 
     * @param userId The user ID
     * @param limit Maximum number of recommendations (default: 10)
     * @return List of personalized products
     */
    @GetMapping("/personalized/{userId}")
    public ResponseEntity<List<Product>> getPersonalizedRecommendations(
            @PathVariable int userId,
            @RequestParam(defaultValue = "10") int limit) {
        
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        // Load data to ML service first
        loadDataToMLService();
        
        // Call Python ML service
        String url = ML_SERVICE_URL + "/recommendations/personalized/" + userId + "?limit=" + limit;
        try {
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }
    
    /**
     * Get all available products
     * 
     * @return List of all products
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return ResponseEntity.ok(products);
    }
} 