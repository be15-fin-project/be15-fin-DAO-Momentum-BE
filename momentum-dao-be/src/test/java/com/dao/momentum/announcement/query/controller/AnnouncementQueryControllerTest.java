package com.dao.momentum.announcement.query.controller;

import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchRequest;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDto;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.service.AnnouncementQueryService;
import com.dao.momentum.common.dto.Pagination;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AnnouncementQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnnouncementQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnnouncementQueryService announcementQueryService;

    @Test
    @DisplayName("공지사항 목록 조회 성공")
    void getAnnouncementList_success() throws Exception {
        // given
        AnnouncementSearchRequest request = new AnnouncementSearchRequest();
        request.setPage(4);
        request.setSize(10);

        List<AnnouncementDto> announcements = List.of(
                AnnouncementDto.builder()
                        .announcementId(2L)
                        .title("신규 복지포인트 제도 도입")
                        .name("김현우")
                        .createdAt(LocalDateTime.of(2025, 6, 1, 10, 0))
                        .build(),
                AnnouncementDto.builder()
                        .announcementId(1L)
                        .title("수정 테스트")
                        .name("장도윤")
                        .createdAt(LocalDateTime.of(2025, 6, 1, 9, 0))
                        .build()
        );

        AnnouncementListResponse mockResponse = AnnouncementListResponse.builder()
                .announcements(announcements)
                .pagination(Pagination.builder()
                        .currentPage(4)
                        .totalPage(4)
                        .totalItems(32)
                        .build())
                .build();

        when(announcementQueryService.getAnnouncementList(any())).thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/announcement")
                        .param("page", "4")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.announcements[0].announcementId").value(2))
                .andExpect(jsonPath("$.data.announcements[0].title").value("신규 복지포인트 제도 도입"))
                .andExpect(jsonPath("$.data.announcements[0].name").value("김현우"))
                .andExpect(jsonPath("$.data.announcements[0].createdAt").value("2025-06-01T10:00:00"))
                .andExpect(jsonPath("$.data.announcements[1].announcementId").value(1))
                .andExpect(jsonPath("$.data.announcements[1].title").value("수정 테스트"))
                .andExpect(jsonPath("$.data.announcements[1].name").value("장도윤"))
                .andExpect(jsonPath("$.data.announcements[1].createdAt").value("2025-06-01T09:00:00"))
                .andExpect(jsonPath("$.data.pagination.currentPage").value(4))
                .andExpect(jsonPath("$.data.pagination.totalPage").value(4))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(32))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").doesNotExist());
    }
}
