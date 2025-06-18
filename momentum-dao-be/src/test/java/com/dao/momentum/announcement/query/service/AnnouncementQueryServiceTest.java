package com.dao.momentum.announcement.query.service;

import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchRequest;
import com.dao.momentum.announcement.query.dto.request.SortDirection;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDto;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.mapper.AnnouncementQueryMapper;
import com.dao.momentum.common.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnouncementQueryServiceTest {

    @InjectMocks
    private AnnouncementQueryService announcementQueryService;

    @Mock
    private AnnouncementQueryMapper announcementQueryMapper;

    @Test
    @DisplayName("공지사항 목록 조회 - 모든 조건을 적용하여 필터링 성공")
    void shouldReturnAnnouncementsMatchingAllConditions() {
        // given
        AnnouncementSearchRequest request = new AnnouncementSearchRequest();
        request.setTitle("복지");
        request.setName("김현우");
        request.setDeptId(2);
        request.setStartDate(LocalDate.of(2025, 6, 15));
        request.setEndDate(LocalDate.of(2025, 6, 16));
        request.setPage(1);
        request.setSize(5);
        request.setSortDirection(SortDirection.ASC);

        List<AnnouncementDto> announcements = List.of(
                AnnouncementDto.builder()
                        .announcementId(1L)
                        .title("복지 혜택 안내")
                        .name("김현우")
                        .createdAt(LocalDateTime.of(2025, 6, 16, 10, 0))
                        .build()
        );

        // stubbing: announcementQueryMapper.findAnnouncementsByCondition(...) 메서드에 대해
        // 특정 조건을 만족하는 `AnnouncementSearchCondition`이 전달되었을 때,
        // 미리 준비해둔 `announcements` 리스트를 반환하도록 설정한다.
        when(announcementQueryMapper.findAnnouncementsByCondition(argThat(condition ->
                // 제목이 "복지"인 공지사항을 검색 조건으로 설정했는지 확인
                condition.getTitle().equals("복지") &&

                // 작성자 이름이 "김현우"인지 확인
                condition.getName().equals("김현우") &&

                // 부서 ID가 2인지 확인
                condition.getDeptId().equals(2) &&

                // 시작 날짜가 2025-06-15로 정확히 설정되었는지 확인
                condition.getStartDate().equals(LocalDate.of(2025, 6, 15)) &&

                // 종료 날짜가 2025-06-16이 아니라, from() 메서드 내부 로직에 의해 +1 된 2025-06-17인지 확인
                condition.getEndDate().equals(LocalDate.of(2025, 6, 17)) &&

                // 한 페이지에 조회할 데이터 개수가 5개인지 확인 (limit)
                condition.getLimit() == 5 &&

                // 페이지 오프셋이 0인지 확인 (page = 1 이므로 offset = (1 - 1) * 5 = 0)
                condition.getOffset() == 0 &&

                // 정렬 방향이 오름차순(ASC)으로 설정되어 있는지 확인
                condition.getSortDirection() == SortDirection.ASC

                // 위 조건을 모두 만족하는 경우에만 이 stubbing이 적용된다.
        ))).thenReturn(announcements);


        when(announcementQueryMapper.countAnnouncementsByCondition(any())).thenReturn(1L);

        // when
        AnnouncementListResponse result = announcementQueryService.getAnnouncementList(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getAnnouncements()).hasSize(1);
        AnnouncementDto dto = result.getAnnouncements().get(0);
        assertThat(dto.getTitle()).contains("복지");
        assertThat(dto.getName()).contains("김현우");
        assertThat(dto.getCreatedAt()).isBetween(
                request.getStartDate().atStartOfDay(),
                request.getEndDate().atTime(23, 59, 59)
        );

        Pagination pagination = result.getPagination();
        assertThat(pagination).isNotNull();
        assertThat(pagination.getCurrentPage()).isEqualTo(1);
        assertThat(pagination.getTotalPage()).isEqualTo(1);
        assertThat(pagination.getTotalItems()).isEqualTo(1);

        verify(announcementQueryMapper).findAnnouncementsByCondition(any());
        verify(announcementQueryMapper).countAnnouncementsByCondition(any());
    }
}
