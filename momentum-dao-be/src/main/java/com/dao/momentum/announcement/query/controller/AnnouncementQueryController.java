package com.dao.momentum.announcement.query.controller;

import com.dao.momentum.announcement.query.dto.request.AnnouncementSearchRequest;
import com.dao.momentum.announcement.query.dto.response.AnnouncementListResponse;
import com.dao.momentum.announcement.query.service.AnnouncementQueryService;
import com.dao.momentum.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementQueryController {

    private final AnnouncementQueryService announcementQueryService;

    @GetMapping
    public ResponseEntity<ApiResponse<AnnouncementListResponse>> getAnnouncementList(
            @Valid @ModelAttribute("search") AnnouncementSearchRequest announcementSearchRequest
    ) {
        AnnouncementListResponse announcementListResponse = announcementQueryService.getAnnouncementList(announcementSearchRequest);

        return ResponseEntity.ok(ApiResponse.success(announcementListResponse));
    }
}
