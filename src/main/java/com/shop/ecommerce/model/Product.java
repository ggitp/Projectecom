package com.shop.ecommerce.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;       // for date and time
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "products")
public class Product {

    // Product's Attributes
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Getter
    @Setter
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @Column(nullable = false)
    private String category;

    @Getter
    @Setter
    @Column(nullable = false)
    private float price;

    @Getter
    @Setter
    @Column(nullable = false)
    private int quantity;

    @Getter
    @Setter
    @Column(name = "image_url")
    private String imageUrl;

    @Getter
    @Setter
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Getter
    @Setter
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Getter
    @Setter
    @Column
    private int views;

    @Getter
    @Setter
    @ElementCollection
    @CollectionTable(name = "product_tags", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "tag")
    private List<String> tags;

    // Default constructor required by JPA
    public Product() {
        this.views = 0;
        this.tags = new ArrayList<String>();
    }

    // Product's Constructor
    public Product(String productName, String description, String category, float price, int quantity, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt, List<String> tags) {
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.views = 0;
        this.tags = tags != null ? tags : new ArrayList<String>();
    }

}