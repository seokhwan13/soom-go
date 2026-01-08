package org.team4.project.domain.service.dto;

import org.team4.project.domain.service.entity.category.Category;
import org.team4.project.domain.service.entity.category.TagService;
import org.team4.project.domain.service.entity.category.type.CategoryType;
import org.team4.project.domain.service.entity.category.type.TagType;
import org.team4.project.domain.service.entity.service.ProjectService;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceeDTO(
        Long id,
        String thumbnail,
        String title,
        Integer price,
        Double rating,
        Long reviewCount,
        String freelancerName,
        CategoryType category,
        TagType[] tags,
        String content,
        LocalDateTime createdAt
) {
    public ServiceeDTO(ProjectService service, List<TagService> tagServices, Category category, Long reviewCount, Double rating, String mainImage) {
        this(
                service.getId(),
                mainImage,
                service.getTitle(),
                service.getPrice(),
                rating, // rating
                reviewCount, // reviewCount
                service.getFreelancer().getNickname(),
                category.getName(),
                tagServices.stream()
                        .map(tagService -> tagService.getTag().getName())
                        .toArray(TagType[]::new),
                service.getContent(),
                service.getCreatedAt()
        );
    }
    public static ServiceeDTO from(ProjectService service, List<TagService> tagServices, Category category, Long reviewCount, Double rating, String mainImage) {
        return new ServiceeDTO(service, tagServices, category, reviewCount, rating, mainImage);
    }

    public ServiceeDTO(ProjectService service, Long reviewCount, Double rating, String mainImage) {
        this(
                service.getId(),
                mainImage,
                service.getTitle(),
                service.getPrice(),
                rating, // rating
                reviewCount, // reviewCount
                service.getFreelancer().getNickname(),
                null,
                null,
                service.getContent(),
                service.getCreatedAt()
        );
    }

    public static ServiceeDTO fromCardOnly(ProjectService service, Long reviewCount, Double rating, String mainImage) {
        return new ServiceeDTO(service, reviewCount, rating, mainImage);
    }
}
