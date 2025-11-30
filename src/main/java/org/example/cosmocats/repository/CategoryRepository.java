package org.example.cosmocats.repository;

import org.example.cosmocats.repository.entity.CategoryEntity;
import org.example.cosmocats.common.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Optional<CategoryEntity> findByType(CategoryType type);

    @Query("SELECT c FROM CategoryEntity c WHERE c.description LIKE %:keyword%")
    List<CategoryEntity> findByDescriptionContaining(@Param("keyword") String keyword);
}