package com.dao.momentum.announcement.query.controller;

import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchRequest;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDetailResponse;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.service.AnnouncementQueryService;
import com.dao.momentum.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
@Tag(name = "공지사항 조회", description = "공지사항 상세 및 목록 조회 API")
public class AnnouncementQueryController {

    private final AnnouncementQueryService announcementQueryService;

    @GetMapping("/{announcementId}")
    @Operation(summary = "공지사항 상세 조회", description = "공지사항 ID를 통해 상세 내용을 조회합니다.")
    public ResponseEntity<ApiResponse<AnnouncementDetailResponse>> getAnnouncement(
            @PathVariable("announcementId") Long announcementId
    ) {
        AnnouncementDetailResponse announcementDetailResponse = announcementQueryService.getAnnouncement(announcementId);
        return ResponseEntity.ok(ApiResponse.success(announcementDetailResponse));
    }

    @GetMapping
    @Operation(summary = "공지사항 목록 조회", description = "검색 조건(제목, 작성자, 부서, 기간 등)을 통해 공지사항 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<AnnouncementListResponse>> getAnnouncementList(
            @Valid @ModelAttribute("search") AnnouncementSearchRequest announcementSearchRequest
    ) {
        AnnouncementListResponse announcementListResponse = announcementQueryService.getAnnouncementList(announcementSearchRequest);
        return ResponseEntity.ok(ApiResponse.success(announcementListResponse));
    }
}
