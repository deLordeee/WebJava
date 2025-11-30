package org.example.cosmocats.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.cosmocats.common.CategoryType;
import org.hibernate.annotations.NaturalId;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_seq")
    @SequenceGenerator(name = "categories_seq", sequenceName = "categories_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @NaturalId
    @Column(name = "type", nullable = false, unique = true, length = 50)
    private CategoryType type;

    @Column(name = "description", length = 500)
    private String description;
}