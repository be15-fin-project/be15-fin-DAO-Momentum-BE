package com.dao.momentum.announcement.command.application.controller;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.service.AnnouncementCommandService;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnouncementCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(username = "123")
class AnnouncementCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnnouncementCommandService announcementCommandService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("공지사항 등록 성공 (파일 포함)")
    void testCreateAnnouncementWithFiles() throws Exception {
        // given
        AnnouncementCreateRequest request = new AnnouncementCreateRequest();
        request.setTitle("제목");
        request.setContent("내용");

        // presigned URL로 업로드된 파일에 대한 정보 추가
        AttachmentRequest attachment = new AttachmentRequest();
        attachment.setS3Key("announcements/uuid/test.png"); // S3에 업로드된 key
        attachment.setType("png");

        request.setAttachments(List.of(attachment));

        AnnouncementCreateResponse response = new AnnouncementCreateResponse(1L);
        when(announcementCommandService.create(any(), any(UserDetails.class))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/announcement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.announcementId").value(1))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("공지사항 수정 성공")
    void testModifyAnnouncement() throws Exception {
        // given
        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정 제목");
        request.setContent("수정 내용");

        AttachmentRequest attachment1 = new AttachmentRequest();
        attachment1.setS3Key("announcements/uuid/image1.png");
        attachment1.setType("png");

        AttachmentRequest attachment2 = new AttachmentRequest();
        attachment2.setS3Key("announcements/uuid/doc1.pdf");
        attachment2.setType("pdf");

        request.setAttachments(List.of(attachment1, attachment2));
        request.setRemainFileIdList(List.of(1L, 2L));

        AnnouncementModifyResponse response = new AnnouncementModifyResponse(1L);
        when(announcementCommandService.modify(any(), eq(1L), any(UserDetails.class))).thenReturn(response);

        // when & then
        mockMvc.perform(put("/announcement/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.announcementId").value(1))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("공지사항 삭제 성공")
    void testDeleteAnnouncement() throws Exception {
        // given
        Long announcementId = 1L;
        doNothing().when(announcementCommandService).delete(eq(announcementId), any(UserDetails.class));

        // when & then
        mockMvc.perform(delete("/announcement/{announcementId}", announcementId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(announcementCommandService).delete(eq(announcementId), any(UserDetails.class));
    }


    @Test
    @DisplayName("공지사항 수정 - 공지사항이 존재하지 않으면 404")
    void testModifyAnnouncement_NotFound() throws Exception {
        // given
        AnnouncementModifyRequest request = new AnnouncementModifyRequest();
        request.setTitle("수정 제목");
        request.setContent("수정 내용");

        AttachmentRequest attachment1 = new AttachmentRequest();
        attachment1.setS3Key("announcements/uuid/image1.png");
        attachment1.setType("png");

        AttachmentRequest attachment2 = new AttachmentRequest();
        attachment2.setS3Key("announcements/uuid/doc1.pdf");
        attachment2.setType("pdf");

        request.setAttachments(List.of(attachment1, attachment2));
        request.setRemainFileIdList(List.of(1L, 2L));

        when(announcementCommandService.modify(any(), eq(999L), any(UserDetails.class)))
                .thenThrow(new NoSuchAnnouncementException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // when & then
        mockMvc.perform(put("/announcement/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.ANNOUNCEMENT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.ANNOUNCEMENT_NOT_FOUND.getMessage()));
    }

}
