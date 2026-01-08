package org.team4.project.domain.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.team4.project.domain.service.dto.ServiceListDTO;

public interface ServiceCustomRepository {
    Page<ServiceListDTO> findAllOrderByReviewCountDesc(Pageable pageable);
    Page<ServiceListDTO> findAllOrderById(Pageable pageable);
}
