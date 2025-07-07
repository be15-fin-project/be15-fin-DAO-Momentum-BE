package com.dao.momentum.announcement.query.controller;

import com.dao.momentum.announcement.query.dto.response.AnnouncementDetailDto;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDetailResponse;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDto;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.service.AnnouncementQueryService;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.file.command.application.dto.response.AttachmentDto;
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
    @DisplayName("공지사항 상세 조회 성공")
    void getAnnouncementDetail_success() throws Exception {
        // given
        Long announcementId = 100L;

        AnnouncementDetailDto dto = AnnouncementDetailDto.builder()
                .announcementId(announcementId)
                .empId(1L)
                .employeeName("김현우")
                .deptId(10)
                .departmentName("인사팀")
                .positionName("사원")
                .title("복지제도 변경 안내")
                .content("복지포인트가 늘어났습니다.")
                .createdAt(LocalDateTime.of(2025, 6, 1, 10, 0))
                .updatedAt(LocalDateTime.of(2025, 6, 2, 12, 0))
                .attachments(List.of(
                        AttachmentDto.builder()
                                .url("https://example.com/file1.pdf")
                                .name("사내복지안내.pdf")
                                .build(),
                        AttachmentDto.builder()
                                .url("https://example.com/file2.pdf")
                                .name("복지포인트규정.pdf")
                                .build()
                ))
                .build();

        AnnouncementDetailResponse response = AnnouncementDetailResponse.builder()
                .announcement(dto)
                .build();

        when(announcementQueryService.getAnnouncement(announcementId)).thenReturn(response);

        // when & then
        mockMvc.perform(get("/announcement/{announcementId}", announcementId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.announcement.announcementId").value(announcementId))
                .andExpect(jsonPath("$.data.announcement.employeeName").value("김현우"))
                .andExpect(jsonPath("$.data.announcement.departmentName").value("인사팀"))
                .andExpect(jsonPath("$.data.announcement.title").value("복지제도 변경 안내"))
                .andExpect(jsonPath("$.data.announcement.attachments[0].url").value("https://example.com/file1.pdf"))
                .andExpect(jsonPath("$.data.announcement.attachments[0].name").value("사내복지안내.pdf"))
                .andExpect(jsonPath("$.data.announcement.attachments[1].url").value("https://example.com/file2.pdf"))
                .andExpect(jsonPath("$.data.announcement.attachments[1].name").value("복지포인트규정.pdf"))
                .andExpect(jsonPath("$.errorCode").doesNotExist())
                .andExpect(jsonPath("$.message").doesNotExist());
    }

    @Test
    @DisplayName("공지사항 목록 조회 성공")
    void getAnnouncementList_success() throws Exception {
        // given
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
