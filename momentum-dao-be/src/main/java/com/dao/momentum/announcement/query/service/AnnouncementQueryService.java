package com.dao.momentum.announcement.query.service;

import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchCondition;
import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchRequest;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDto;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.mapper.AnnouncementQueryMapper;
import com.dao.momentum.common.dto.Pagination;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementQueryService {
    private final AnnouncementQueryMapper announcementQueryMapper;

    public AnnouncementListResponse getAnnouncementList(AnnouncementSearchRequest request) {
        // 요청 객체의 날짜 범위가 유효한지 검사
        request.validateDateRange();

        AnnouncementSearchCondition searchCondition = AnnouncementSearchCondition.from(request);

        List<AnnouncementDto> announcements = announcementQueryMapper.findAnnouncementsByCondition(searchCondition);
        long totalItemCount = announcementQueryMapper.countAnnouncementsByCondition(searchCondition);

        int currentPage = request.getPage();
        int size = request.getSize();
        int totalPage = size > 0 ? (int) Math.ceil((double) totalItemCount / size) : 1;

        Pagination pagination = Pagination.builder()
                .currentPage(currentPage)
                .totalPage(totalPage)
                .totalItems(totalItemCount)
                .build();

        return AnnouncementListResponse.builder()
                .announcements(announcements)
                .pagination(pagination)
                .build();
    }
}
