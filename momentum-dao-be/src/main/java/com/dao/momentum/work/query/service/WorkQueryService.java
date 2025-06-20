package com.dao.momentum.work.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.work.query.dto.request.AdminWorkSearchDTO;
import com.dao.momentum.work.query.dto.request.AdminWorkSearchRequest;
import com.dao.momentum.work.query.dto.request.WorkSearchDTO;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.WorkDTO;
import com.dao.momentum.work.query.dto.response.WorkListResponse;
import com.dao.momentum.work.query.mapper.WorkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkQueryService {

    private final WorkMapper workMapper;

    @Transactional(readOnly = true)
    public WorkListResponse getMyWorks(UserDetails userDetails, WorkSearchRequest workSearchRequest) {
        long empId = Long.parseLong(userDetails.getUsername());

        WorkSearchDTO workSearchDTO = workSearchRequest.toDTO();

        List<WorkDTO> works = workMapper.getMyWorks(workSearchDTO, empId);

        return WorkListResponse.builder()
                .works(works)
                .pagination(null)
                .build();
    }

    @Transactional(readOnly = true)
    public WorkListResponse getWorks(AdminWorkSearchRequest adminWorkSearchRequest) {

        AdminWorkSearchDTO adminWorkSearchDTO = adminWorkSearchRequest.toDTO();

        List<WorkDTO> works = workMapper.getWorks(adminWorkSearchDTO);
        int page = adminWorkSearchDTO.getPage();
        int size = adminWorkSearchDTO.getSize();

        long totalItems = workMapper.countWorks(adminWorkSearchDTO);

        return WorkListResponse.builder()
                .works(works)
                .pagination(
                        Pagination.builder()
                                .currentPage(page)
                                .totalItems(totalItems)
                                .totalPage((int) Math.ceil((double) totalItems / size))
                                .build()
                )
                .build();
    }

}