package com.shop.ecommerce.dto;

import java.util.List;

public record SuggestResponseDto(
        String query,
        List<ProductSuggestionDto> suggestions
) {}