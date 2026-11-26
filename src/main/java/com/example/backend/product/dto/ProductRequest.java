package com.example.backend.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "name must not be blank")
        @Size(max = 200, message = "name must be at most 200 characters")
        String name,

        @Size(max = 1000, message = "description must be at most 1000 characters")
        String description,

        @NotNull(message = "price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "price must be greater than 0")
        @Digits(integer = 10, fraction = 2, message = "price must have at most 10 integer and 2 fraction digits")
        BigDecimal price,

        @NotNull(message = "stock is required")
        @Min(value = 0, message = "stock must not be negative")
        Integer stock
) {
}
