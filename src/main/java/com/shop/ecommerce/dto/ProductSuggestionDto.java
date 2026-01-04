package com.shop.ecommerce.dto;

public record ProductSuggestionDto(
        long id,
        String name,
        String category,
        float price,
        String imageUrl
) {}