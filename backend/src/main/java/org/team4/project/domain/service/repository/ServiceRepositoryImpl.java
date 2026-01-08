package org.team4.project.domain.service.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.team4.project.domain.service.dto.QServiceListDTO;
import org.team4.project.domain.service.dto.ServiceListDTO;
import org.team4.project.domain.service.entity.resource.QServiceResource;
import org.team4.project.domain.service.entity.reviews.QServiceReview;
import org.team4.project.domain.service.entity.service.QProjectService;

import java.util.List;

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

        Long total = queryFactory
                .select(s.id.countDistinct())
                .from(s)
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
