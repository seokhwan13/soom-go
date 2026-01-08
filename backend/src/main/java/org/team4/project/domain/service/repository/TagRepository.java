package org.team4.project.domain.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.team4.project.domain.service.entity.category.Category;
import org.team4.project.domain.service.entity.category.Tag;
import org.team4.project.domain.service.entity.category.type.TagType;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag,Long> {
    Optional<Tag> findByName(@Param("name") TagType name);
    List<Tag> findAllByNameIn(@Param("names") List<TagType> names);
}
