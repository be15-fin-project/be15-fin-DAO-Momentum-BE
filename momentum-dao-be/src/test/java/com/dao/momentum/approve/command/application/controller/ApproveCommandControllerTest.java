package com.dao.momentum.approve.command.application.controller;

import com.dao.momentum.approve.command.application.service.ApprovalDecisionCommandService;
import com.dao.momentum.approve.command.application.service.ApproveCommandService;
import com.dao.momentum.approve.command.application.service.OcrService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApproveCommandController.class)
@AutoConfigureMockMvc
class ApproveCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApproveCommandService approveCommandService;

    @MockitoBean
    private OcrService ocrService;

    @MockitoBean
    private ApprovalDecisionCommandService approvalDecisionCommandService;

    @DisplayName("참조 결재 열람")
    @WithMockUser(username = "1")
    @Test
    void viewAsReference_success() throws Exception {
        Long documentId = 1L;
        Long empId      = 1L;

        mockMvc.perform(patch("/approval/documents/{documentId}/reference", documentId).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(approveCommandService).viewAsReference(documentId, empId);
    }
}
