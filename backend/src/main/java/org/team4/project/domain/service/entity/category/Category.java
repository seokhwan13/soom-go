package org.team4.project.domain.service.entity.category;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.team4.project.domain.service.entity.category.type.CategoryType;
import org.team4.project.global.jpa.entity.BaseEntity;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private CategoryType name;

    @OneToMany(mappedBy = "category")
    private List<Tag> tags;

    public Category(CategoryType name) {
        this.name = name;
    }
}
