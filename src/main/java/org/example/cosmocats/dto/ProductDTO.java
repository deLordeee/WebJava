package org.example.cosmocats.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cosmocats.dto.validation.CosmicWordCheck;

import java.math.BigDecimal;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Product name must be between 2 and 100 characters")
    @CosmicWordCheck(message = "Product name must contain specific cosmic-related terms like 'star', 'galaxy', 'comet', 'space', 'cosmic', etc.")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Digits(integer = 10, fraction = 2, message = "Price must have up to 10 integer digits and 2 fraction digits")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotBlank(message = "Category is required")
    @Pattern(regexp = "ANTI_GRAVITY_TOYS|COSMIC_FOOD|SPACE_ACCESSORIES|INTERGALACTIC_PETS",
            message = "Category must be one of: ANTI_GRAVITY_TOYS, COSMIC_FOOD, SPACE_ACCESSORIES, INTERGALACTIC_PETS")
    private String category;

    @Pattern(regexp = "AVAILABLE|OUT_OF_STOCK|DISCONTINUED",
            message = "Status must be one of: AVAILABLE, OUT_OF_STOCK, DISCONTINUED")
    private String status;
}
