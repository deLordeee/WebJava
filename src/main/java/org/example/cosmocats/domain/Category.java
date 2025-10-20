package org.example.cosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cosmocats.common.CategoryType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private CategoryType type;
    private String description;
}
