package org.team4.project.domain.service.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.team4.project.domain.service.entity.category.type.CategoryType;
import org.team4.project.domain.service.entity.category.type.TagType;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ServiceListDTO {

    private Long id;
    private Long thumbnailId;
    private String title;
    private Integer price;
    private Double rating;
    private Long reviewCount;
    private String freelancerName;
    private CategoryType category;
    private List<TagType> tags;
    private LocalDateTime createdAt;

    @QueryProjection
    public ServiceListDTO(
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
        this.id = id;
        this.thumbnailId = thumbnailId;
        this.title = title;
        this.price = price;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.freelancerName = freelancerName;
        this.category = category;
        this.createdAt = createdAt;
    }

}
