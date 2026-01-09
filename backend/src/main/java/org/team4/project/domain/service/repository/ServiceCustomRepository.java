package org.team4.project.domain.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.team4.project.domain.service.dto.ServiceListDTO;
import org.team4.project.domain.service.entity.category.Category;
import org.team4.project.domain.service.entity.category.Tag;
import org.team4.project.domain.service.entity.category.type.CategoryType;
import org.team4.project.domain.service.entity.category.type.TagType;

import java.util.List;

public interface ServiceCustomRepository {
    Page<ServiceListDTO> findAllOrderByReviewCountDesc(Pageable pageable);
    Page<ServiceListDTO> findAllOrderById(Pageable pageable);
    Page<ServiceListDTO> findAllByKeywordOrderById(String keyword, Pageable pageable);
    Page<ServiceListDTO> findAllByCategoryOrderById(CategoryType category, Pageable pageable);
    Page<ServiceListDTO> findAllByTagsOrderById(List<TagType> tags, Pageable pageable);
}
