package com.exam.recipesystem.specification;

import com.exam.recipesystem.entity.Rating;
import com.exam.recipesystem.entity.Recipe;
import com.exam.recipesystem.enums.RecipeStatus;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

public final class RecipeSpecification {

    private RecipeSpecification() {
    }

    public static Specification<Recipe> withFilters(
            Long categoryId,
            Long userId,
            RecipeStatus status,
            String keyword,
            Integer minRating,
            Integer maxCookingTimeMinutes,
            LocalDateTime createdAfter,
            LocalDateTime createdBefore) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }
            if (userId != null) {
                predicates.add(cb.equal(root.get("user").get("id"), userId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("description")), like),
                        cb.like(cb.lower(root.get("ingredients")), like),
                        cb.like(cb.lower(root.get("instructions")), like)));
            }
            if (maxCookingTimeMinutes != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("cookingTimeMinutes"), maxCookingTimeMinutes));
            }
            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
            }
            if (createdBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), createdBefore));
            }
            if (minRating != null) {
                Subquery<Double> averageQuery = query.subquery(Double.class);
                Root<Rating> ratingRoot = averageQuery.from(Rating.class);
                averageQuery.select(cb.avg(ratingRoot.get("value")));
                averageQuery.where(cb.equal(ratingRoot.get("recipe"), root));
                predicates.add(cb.greaterThanOrEqualTo(averageQuery, minRating.doubleValue()));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
