package com.dao.momentum.work.query.service;

import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.WorkDTO;
import com.dao.momentum.work.query.dto.response.WorkListResponse;
import com.dao.momentum.work.query.mapper.WorkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkQueryService {

    private final WorkMapper workMapper;

    @Transactional(readOnly = true)
    public WorkListResponse getMyWorks(UserDetails userDetails, WorkSearchRequest workSearchRequest) {
        long empId = Long.parseLong(userDetails.getUsername());
            LocalDate rangeStartDate = workSearchRequest.getRangeStartDate() == null ?
                    null : workSearchRequest.getRangeStartDate();
            LocalDate rangeEndDate = workSearchRequest.getRangeEndDate() == null ?
                    null : workSearchRequest.getRangeEndDate().plusDays(1);

        // EndDate에 지정한 날짜를 포함하려면 LocalDateTime 기준으로 하루 더 있어야 함

        List<WorkDTO> works = workMapper.getMyWorks(rangeStartDate, rangeEndDate, empId);

        return WorkListResponse.builder()
                .works(works)
                .build();
    }

//    @Transactional(readOnly = true)
//    public WorkListResponse getWorks(UserDetails userDetails, AdminWorkSearchRequest workSearchRequest) {
//        List<WorkDTO> works = workMapper.getWorks();
//
//        return WorkListResponse.builder()
//                .works(works)
//                .build();
//    }

}