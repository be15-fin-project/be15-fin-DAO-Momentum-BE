package com.dao.momentum.work.command.application.controller;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.dto.request.WorkStartRequest;
import com.dao.momentum.work.command.application.dto.response.WorkStartResponse;
import com.dao.momentum.work.command.application.dto.response.WorkSummaryDTO;
import com.dao.momentum.work.command.application.service.WorkCommandService;
import com.dao.momentum.work.exception.WorkException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WorkCommandController.class)
@AutoConfigureMockMvc
class WorkCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkCommandService workCommandService;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Test
    void 출근_성공_850_출근등록() throws Exception {
        WorkStartRequest request = WorkStartRequest.builder()
                .startPushedAt(LocalDateTime.of(2025, 6, 13, 8, 50, 40))
                .build(); // 생성자에 맞게 초기화
        WorkStartResponse response = WorkStartResponse.builder()
                .workSummaryDTO(
                        WorkSummaryDTO.builder()
                                .workId(1)
                                .startAt(LocalDateTime.of(2025, 6, 13, 9, 0, 0))
                                .endAt(LocalDateTime.of(2025, 6, 13, 18, 0, 0))
                                .breakTime(60)
                                .workTime(480)
                                .build()
                ).message("")
                .build();

        given(workCommandService.createWork(any(), any(), any())).willReturn(response);

        mockMvc.perform(post("/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user("곽진웅").password("Test1234!").roles("ADMIN"))
                        .with(csrf())
                        .with(req -> {
                            req.setRemoteAddr("127.0.0.1");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.workSummaryDTO.startAt").value("2025-06-13T09:00:00"))
                .andExpect(jsonPath("$.data.workSummaryDTO.endAt").value("2025-06-13T18:00:00"));
    }

    @Test
    void 출근_성공_910_출근등록() throws Exception {
        WorkStartRequest request = WorkStartRequest.builder()
                .startPushedAt(LocalDateTime.of(2025, 6, 13, 9, 10, 40))
                .build(); // 생성자에 맞게 초기화
        WorkStartResponse response = WorkStartResponse.builder()
                .workSummaryDTO(
                        WorkSummaryDTO.builder()
                                .workId(1)
                                .startAt(LocalDateTime.of(2025, 6, 13, 9, 10, 0))
                                .endAt(LocalDateTime.of(2025, 6, 13, 18, 0, 0))
                                .breakTime(60)
                                .workTime(470)
                                .build()
                ).message("")
                .build();

        given(workCommandService.createWork(any(), any(), any())).willReturn(response);

        mockMvc.perform(post("/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user("곽진웅").password("Test1234!").roles("ADMIN"))
                        .with(csrf())
                        .with(req -> {
                            req.setRemoteAddr("127.0.0.1");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.workSummaryDTO.startAt").value("2025-06-13T09:10:00"))
                .andExpect(jsonPath("$.data.workSummaryDTO.endAt").value("2025-06-13T18:00:00"));
    }

    @Test
    void 출근_실패_허용IP없음() throws Exception {
        WorkStartRequest request = WorkStartRequest.builder()
                .startPushedAt(LocalDateTime.of(2025, 6, 13, 8, 50, 40))
                .build();

        // IP 미허용 상황을 가정해 예외 발생
        given(workCommandService.createWork(any(), any(), any()))
                .willThrow(new WorkException(ErrorCode.IP_NOT_ALLOWED));

        mockMvc.perform(post("/works")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(user("곽진웅").password("Test1234!").roles("ADMIN"))
                        .with(csrf())
                        .with(req -> {
                            req.setRemoteAddr("192.168.0.123");
                            return req;
                        }))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("출퇴근 등록이 불가능한 IP입니다."));
    }
}
