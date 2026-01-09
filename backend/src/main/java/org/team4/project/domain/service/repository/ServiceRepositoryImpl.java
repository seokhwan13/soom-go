package org.team4.project.domain.service.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.team4.project.domain.service.dto.QServiceListDTO;
import org.team4.project.domain.service.dto.ServiceListDTO;
import org.team4.project.domain.service.entity.category.QTagService;
import org.team4.project.domain.service.entity.category.Tag;
import org.team4.project.domain.service.entity.category.type.CategoryType;
import org.team4.project.domain.service.entity.category.type.TagType;
import org.team4.project.domain.service.entity.resource.QServiceResource;
import org.team4.project.domain.service.entity.reviews.QServiceReview;
import org.team4.project.domain.service.entity.service.QProjectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ServiceRepositoryImpl implements ServiceCustomRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ServiceListDTO> findAllOrderByReviewCountDesc(Pageable pageable) {

        QProjectService s = QProjectService.projectService;
        QServiceReview sr = QServiceReview.serviceReview;
        QServiceResource r = QServiceResource.serviceResource;

        List<ServiceListDTO> content = queryFactory
                .select(new QServiceListDTO(
                        s.id,
                        r.id.min(),
                        s.title,
                        s.price,
                        sr.rating.avg(),
                        sr.count(),
                        s.freelancer.nickname,
                        s.category.name,
                        s.createdAt
                ))
                .from(s)
                .leftJoin(sr).on(sr.service.eq(s))
                .leftJoin(s.resources, r)
                .groupBy(s.id)
                .orderBy(sr.count().desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        mapTagsToServiceList(content);

        Long total = queryFactory
                .select(s.count())
                .from(s)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ServiceListDTO> findAllOrderById(Pageable pageable) {

        QProjectService s = QProjectService.projectService;
        QServiceReview sr = QServiceReview.serviceReview;
        QServiceResource r = QServiceResource.serviceResource;

        List<ServiceListDTO> content = queryFactory
                .select(new QServiceListDTO(
                        s.id,
                        r.id.min(),
                        s.title,
                        s.price,
                        sr.rating.avg(),
                        sr.count(),
                        s.freelancer.nickname,
                        s.category.name,
                        s.createdAt
                ))
                .from(s)
                .leftJoin(sr).on(sr.service.eq(s))
                .leftJoin(s.resources, r)
                .groupBy(s.id)
                .orderBy(s.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        mapTagsToServiceList(content);

        Long total = queryFactory
                .select(s.id.countDistinct())
                .from(s)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ServiceListDTO> findAllByKeywordOrderById(String keyword, Pageable pageable) {
        QProjectService s = QProjectService.projectService;
        QServiceReview sr = QServiceReview.serviceReview;
        QServiceResource r = QServiceResource.serviceResource;

        List<ServiceListDTO> content = queryFactory
                .select(new QServiceListDTO(
                        s.id,
                        r.id.min(),
                        s.title,
                        s.price,
                        sr.rating.avg(),
                        sr.count(),
                        s.freelancer.nickname,
                        s.category.name,
                        s.createdAt
                ))
                .from(s)
                .leftJoin(sr).on(sr.service.eq(s))
                .leftJoin(s.resources, r)
                .where(s.title.containsIgnoreCase(keyword))
                .groupBy(s.id)
                .orderBy(s.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        mapTagsToServiceList(content);

        Long total = queryFactory
                .select(s.id.countDistinct())
                .from(s)
                .where(s.title.containsIgnoreCase(keyword))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ServiceListDTO> findAllByCategoryOrderById(CategoryType category, Pageable pageable) {
        QProjectService s = QProjectService.projectService;
        QServiceReview sr = QServiceReview.serviceReview;
        QServiceResource r = QServiceResource.serviceResource;

        List<ServiceListDTO> content = queryFactory
                .select(new QServiceListDTO(
                        s.id,
                        r.id.min(),
                        s.title,
                        s.price,
                        sr.rating.avg(),
                        sr.count(),
                        s.freelancer.nickname,
                        s.category.name,
                        s.createdAt
                ))
                .from(s)
                .leftJoin(sr).on(sr.service.eq(s))
                .leftJoin(s.resources, r)
                .where(s.category.name.eq(category))
                .groupBy(s.id)
                .orderBy(s.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        mapTagsToServiceList(content);

        Long total = queryFactory
                .select(s.id.countDistinct())
                .from(s)
                .where(s.category.name.eq(category))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<ServiceListDTO> findAllByTagsOrderById(List<TagType> tags, Pageable pageable) {

        QProjectService s = QProjectService.projectService;
        QServiceReview sr = QServiceReview.serviceReview;
        QServiceResource r = QServiceResource.serviceResource;
        QTagService ts = QTagService.tagService;

        List<ServiceListDTO> content = queryFactory
                .select(new QServiceListDTO(
                        s.id,
                        r.id.min(),
                        s.title,
                        s.price,
                        sr.rating.avg(),
                        sr.id.countDistinct(),
                        s.freelancer.nickname,
                        s.category.name,
                        s.createdAt
                ))
                .from(s)
                .join(s.tags, ts)
                .leftJoin(sr).on(sr.service.eq(s))
                .leftJoin(s.resources, r)
                .where(ts.tag.name.in(tags))
                .groupBy(s.id)
                .having(ts.tag.countDistinct().eq((long) tags.size()))
                .orderBy(s.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        mapTagsToServiceList(content);

        Long total = queryFactory
                .select(s.id.countDistinct())
                .from(s)
                .join(s.tags, ts)
                .where(ts.tag.name.in(tags))
                .groupBy(s.id)
                .having(ts.tag.countDistinct().eq((long) tags.size()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private void mapTagsToServiceList(List<ServiceListDTO> services) {
        QTagService ts = QTagService.tagService;

        List<Long> serviceIds = services.stream()
                .map(ServiceListDTO::getId)
                .toList();

        List<Tuple> tagTuples = queryFactory
                .select(ts.service.id, ts.tag.name)
                .from(ts)
                .where(ts.service.id.in(serviceIds))
                .fetch();

        Map<Long, List<TagType>> tagMap = new HashMap<>();

        for (Tuple t : tagTuples) {
            tagMap
                .computeIfAbsent(t.get(ts.service.id), k -> new ArrayList<>())
                .add(t.get(ts.tag.name));
        }

        services.forEach(dto ->
                dto.setTags(tagMap.getOrDefault(dto.getId(), List.of()))
        );

    }
}
