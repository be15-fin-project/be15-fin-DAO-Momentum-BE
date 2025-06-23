package com.dao.momentum.file.command.application.controller;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.file.command.application.dto.request.DownloadUrlRequest;
import com.dao.momentum.file.command.application.dto.request.FilePresignedUrlRequest;
import com.dao.momentum.file.command.application.dto.response.DownloadUrlResponse;
import com.dao.momentum.file.command.application.dto.response.FilePresignedUrlResponse;
import com.dao.momentum.file.command.application.service.FileService;
import com.dao.momentum.file.exception.FileUploadFailedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc(addFilters = false)
class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Presigned URL 생성 성공")
    void generatePresignedUrl_success() throws Exception {
        FilePresignedUrlRequest request = new FilePresignedUrlRequest("test.png", 1024L, "image/png");
        FilePresignedUrlResponse response = new FilePresignedUrlResponse("test-key", "https://presigned.url");

        when(fileService.generatePresignedUrl(any())).thenReturn(response);

        mockMvc.perform(post("/file/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.s3Key").value("https://presigned.url"))
                .andExpect(jsonPath("$.data.presignedUrl").value("test-key"));
    }

    @Test
    @DisplayName("Download URL 생성 성공")
    void generateDownloadUrl_success() throws Exception {
        DownloadUrlRequest request = new DownloadUrlRequest("announcements/5634f623-9646-4103-bb8d-3f4d92b35693/test.png");
        DownloadUrlResponse response = new DownloadUrlResponse("https://download.url");

        when(fileService.generateDownloadUrl("announcements/5634f623-9646-4103-bb8d-3f4d92b35693/test.png")).thenReturn(response);

        mockMvc.perform(post("/file/download-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.signedUrl").value("https://download.url"));
    }

    @Test
    @DisplayName("Presigned URL 생성 실패 - 파일 용량 초과")
    void generatePresignedUrl_fail_fileTooLarge() throws Exception {
        FilePresignedUrlRequest request = new FilePresignedUrlRequest("large_file.pdf", 20 * 1024 * 1024L, "application/pdf");

        when(fileService.generatePresignedUrl(any()))
                .thenThrow(new FileUploadFailedException(ErrorCode.FILE_TOO_LARGE));

        mockMvc.perform(post("/file/presigned-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value(ErrorCode.FILE_TOO_LARGE.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.FILE_TOO_LARGE.getMessage()));
    }
}
