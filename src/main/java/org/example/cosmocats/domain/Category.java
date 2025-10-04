package org.example.cosmocats.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.cosmocats.common.CategoryType;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private CategoryType type;
    private String description;
}
