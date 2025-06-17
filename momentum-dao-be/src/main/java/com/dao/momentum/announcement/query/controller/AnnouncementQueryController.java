package com.dao.momentum.announcement.query.controller;

import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchRequest;
import com.dao.momentum.announcement.query.dto.response.AnnouncementDetailResponse;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.service.AnnouncementQueryService;
import com.dao.momentum.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementQueryController {

    private final AnnouncementQueryService announcementQueryService;

    @GetMapping("/{announcementId}")
    public ResponseEntity<ApiResponse<AnnouncementDetailResponse>> getAnnouncement(
            @PathVariable("announcementId") Long announcementId
    ) {
        AnnouncementDetailResponse announcementDetailResponse = announcementQueryService.getAnnouncement(announcementId);

        return ResponseEntity.ok(ApiResponse.success(announcementDetailResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AnnouncementListResponse>> getAnnouncementList(
            @Valid @ModelAttribute("search") AnnouncementSearchRequest announcementSearchRequest
    ) {
        AnnouncementListResponse announcementListResponse = announcementQueryService.getAnnouncementList(announcementSearchRequest);

        return ResponseEntity.ok(ApiResponse.success(announcementListResponse));
    }
}
