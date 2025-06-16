package com.dao.momentum.announcement.command.application.controller;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.service.AnnouncementCommandService;
import com.dao.momentum.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementCommandController {

    private final AnnouncementCommandService announcementCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementCreateResponse>> createAnnouncement(
            @RequestPart("announcement") AnnouncementCreateRequest announcementCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        AnnouncementCreateResponse announcementCreateResponse = announcementCommandService.create(announcementCreateRequest, files, userDetails);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(announcementCreateResponse));
    }

}
