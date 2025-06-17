package com.dao.momentum.announcement.command.application.controller;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.service.AnnouncementCommandService;
import com.dao.momentum.announcement.exception.AnnouncementAccessDeniedException;
import com.dao.momentum.announcement.exception.FileUploadFailedException;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
public class AnnouncementCommandController {

    private final AnnouncementCommandService announcementCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<AnnouncementCreateResponse>> createAnnouncement(
            @RequestPart("announcement") @Valid AnnouncementCreateRequest announcementCreateRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        AnnouncementCreateResponse announcementCreateResponse = announcementCommandService.create(announcementCreateRequest, files, userDetails);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(announcementCreateResponse));
    }

    @PutMapping("{announcementId}")
    public ResponseEntity<ApiResponse<AnnouncementModifyResponse>> modifyAnnouncement(
            @RequestPart("announcement") @Valid AnnouncementModifyRequest announcementModifyRequest,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @PathVariable("announcementId") Long announcementId,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        AnnouncementModifyResponse announcementModifyResponse = announcementCommandService.modify(announcementModifyRequest, files, announcementId, userDetails);

        return ResponseEntity
                .ok(ApiResponse.success(announcementModifyResponse));
    }

    @DeleteMapping("{announcementId}")
    public ResponseEntity<ApiResponse<Void>> deleteAnnouncement(
            @PathVariable("announcementId") Long announcementId,
            @AuthenticationPrincipal UserDetails userDetails)
    {
        announcementCommandService.delete(announcementId, userDetails);

        return ResponseEntity
                .ok(ApiResponse.success(null));
    }

    @ExceptionHandler(AnnouncementAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAnnouncementAccessDeniedException(AnnouncementAccessDeniedException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(NoSuchAnnouncementException.class)
    public ResponseEntity<ApiResponse<Void>> NoSuchAnnouncementException(NoSuchAnnouncementException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ApiResponse<Void>> FileUploadFailedException(FileUploadFailedException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
