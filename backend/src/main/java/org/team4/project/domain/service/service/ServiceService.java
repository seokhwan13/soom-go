package org.team4.project.domain.service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.team4.project.domain.file.entity.File;
import org.team4.project.domain.file.repository.FileRepository;
import org.team4.project.domain.member.entity.Member;
import org.team4.project.domain.member.repository.MemberRepository;
import org.team4.project.domain.service.dto.*;
import org.team4.project.domain.service.entity.category.Category;
import org.team4.project.domain.service.entity.category.Tag;
import org.team4.project.domain.service.entity.category.TagService;
import org.team4.project.domain.service.entity.category.type.CategoryType;
import org.team4.project.domain.service.entity.category.type.TagType;
import org.team4.project.domain.service.entity.resource.ServiceResource;
import org.team4.project.domain.service.entity.reviews.ServiceReview;
import org.team4.project.domain.service.entity.service.ProjectService;
import org.team4.project.domain.service.exception.ServiceException;
import org.team4.project.domain.service.repository.*;
import org.team4.project.domain.service.repository.ServiceRepository;
import org.team4.project.domain.service.repository.ServiceReviewRepository;
import org.team4.project.domain.service.repository.TagRepository;
import org.team4.project.domain.service.repository.TagServiceRepository;
import org.team4.project.global.security.CustomUserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {
    private final ServiceRepository serviceRepository;
    private final TagServiceRepository tagServiceRepository;
    private final TagRepository tagRepository;
    private final ServiceReviewRepository serviceReviewRepository;
    private final MemberRepository memberRepository;
    private final ServiceResourceRepository serviceResourceRepository;
    private final FileRepository fileRepository;

    //서비스 개수 조회
    public Integer count() {
        return (int) serviceRepository.count();
    }

    //CREATE------------------------------------------------------------------
    // 서비스 생성
    @Transactional
    public ProjectService createService(ServiceCreateRqBody serviceCreateRqBody, CustomUserDetails memberDetails) {
        Member freelancer = memberRepository.findByEmail(memberDetails.getEmail())
                .orElseThrow(() -> new ServiceException("해당 사용자가 존재하지 않습니다."));

        List<Tag> tags = tagRepository.findAllByNameIn(serviceCreateRqBody.tagNames());
        Category category = tags.getFirst().getCategory();
        ProjectService service = serviceRepository.save(ProjectService.addService(serviceCreateRqBody, freelancer, category));

        Map<TagType, Tag> tagMap = tags.stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));
        List<TagService> tagServices = serviceCreateRqBody.tagNames().stream()
                .map(tagType -> {
                    Tag tag = tagMap.get(tagType);
                    if (tag == null) throw new ServiceException("해당 태그가 존재하지 않습니다.");
                    return new TagService(tag, service);
                })
                .toList();
        tagServiceRepository.saveAll(tagServices);


//        List<File> files = fileRepository.findAllByS3UrlIn(serviceCreateRqBody.imageUrls());
//        Map<String, File> fileMap = files.stream()
//                .collect(Collectors.toMap(File::getS3Url, Function.identity()));
//
//        List<ServiceResource> resources = serviceCreateRqBody.imageUrls().stream()
//                .map(url -> {
//                    File file = fileMap.get(url);
//                    if (file == null) throw new ServiceException("해당 파일이 존재하지 않습니다.");
//                    boolean isMain = url.equals(serviceCreateRqBody.mainImageUrl());
//                    return new ServiceResource(file, service, isMain);
//                })
//                .collect(Collectors.toList());

        return service;
    }

    //READ------------------------------------------------------------------
    //서비스 단건 조회 엔티티
    public ProjectService findById(Long id) {
        return serviceRepository
                .findById(id)
                .orElseThrow(() -> new ServiceException("해당 서비스가 존재하지 않습니다."));
    }

    //서비스 단건 조회 DTO
    public ServiceDetailDTO fromFindById(Long id) {
        ProjectService service = findById(id);
        List<TagService> tagServices = findByService(service);
        Category category = tagServices.getFirst().getTag().getCategory();
        Integer reviewCount = serviceReviewRepository.countByServiceId(id);
        Float rating = serviceReviewRepository.findAvgRatingByService(id);

        List<ServiceResource> resources = serviceResourceRepository.findByService(service);
        List<String> imageUrls = resources.stream()
                .map(resource -> {
                    return resource.getFile().getS3Url();
                })
                .toList();
        String mainImage = serviceResourceRepository.findByProjectServiceAndIsRepresentative(id)
                .map(resource -> resource.getFile().getS3Url())
                .orElse(null);
        return new ServiceDetailDTO(service, tagServices, category, reviewCount, rating, imageUrls, mainImage);
    }

    //서비스 다건 조회
    public Page<ServiceListDTO> getServices(Pageable pageable) {
        return serviceRepository.findAllOrderById(pageable);
    }

    //추천 서비스 다건 조회
    public Page<ServiceListDTO> getRecommendationServices(Pageable pageable) {
        return serviceRepository.findAllOrderByReviewCountDesc(pageable);
    }

    //서비스 검색 다건 조회
    public Page<ServiceListDTO> getSearchedServices(String keyWord, Pageable pageable) {
        return serviceRepository.findAllByKeywordOrderById(keyWord, pageable);
    }

    //서비스 다건 조회 (카테고리)
    public Page<ServiceListDTO> getServicesByCategory(Pageable pageable, CategoryType category) {
        return serviceRepository.findAllByCategoryOrderById(category, pageable);
    }

    //서비스 다건 조회 (태그)
    public Page<ServiceListDTO> getServicesByTags(Pageable pageable, List<TagType> tags) {
        return serviceRepository.findAllByTagsOrderById(tags, pageable);
    }

    //UPDATE------------------------------------------------------------------
    //서비스 수정
    public void updateService(Long id, ServiceCreateRqBody serviceCreateRqBody) {
        ProjectService existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new ServiceException("해당 서비스가 존재하지 않습니다."));

        existingService.modify(
                serviceCreateRqBody.title(),
                serviceCreateRqBody.content(),
                serviceCreateRqBody.price()
        );

        serviceRepository.save(existingService);
    }

    //DELETE------------------------------------------------------------------
    //서비스 삭제
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ServiceException("해당 서비스가 존재하지 않습니다.");
        }
        serviceRepository.deleteById(id);
    }

    //서비스 내부 로직
    private List<TagService> findByService(ProjectService service) {
        List<TagService> tagServices = tagServiceRepository.findByService(service);
        if (tagServices == null || tagServices.isEmpty()) {
            throw new ServiceException("해당 서비스의 태그가 존재하지 않습니다.");
        }
        return tagServices;
    }

    // email 기반으로 서비스 목록 찾기
    public Page<ServiceDTO> getServicesByEmail(String username, Pageable pageable) {
        return serviceRepository.findAllByFreelancer_Email(username, pageable)
                .map((service)->{
                    String mainImage = serviceResourceRepository.findByProjectServiceAndIsRepresentative(service.getId())
                            .map(resource -> resource.getFile().getS3Url())
                            .orElse(null);

                    double avgRating = service.getReviews().stream()
                            .mapToDouble(ServiceReview::getRating)
                            .average()
                            .orElse(0.0);
                    return ServiceDTO.fromCardOnly(service, service.getReviews().size(), (float) avgRating, mainImage);
                });
    }
}
