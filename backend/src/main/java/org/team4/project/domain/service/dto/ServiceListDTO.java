package org.team4.project.domain.service.dto;

import com.querydsl.core.annotations.QueryProjection;
import org.team4.project.domain.service.entity.category.type.CategoryType;

import java.time.LocalDateTime;

public record ServiceListDTO(
        Long id,
        Long thumbnailId,
        String title,
        Integer price,
        Double rating,
        Long reviewCount,
        String freelancerName,
        CategoryType category,
        LocalDateTime createdAt
) {
    @QueryProjection
    public ServiceListDTO {}
}
