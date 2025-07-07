package com.dao.momentum.announcement.command.application.controller;

import com.dao.momentum.announcement.command.application.dto.request.AnnouncementCreateRequest;
import com.dao.momentum.announcement.command.application.dto.request.AnnouncementModifyRequest;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementCreateResponse;
import com.dao.momentum.announcement.command.application.dto.response.AnnouncementModifyResponse;
import com.dao.momentum.announcement.command.application.service.AnnouncementCommandService;
import com.dao.momentum.announcement.exception.AnnouncementAccessDeniedException;
import com.dao.momentum.file.exception.FileUploadFailedException;
import com.dao.momentum.announcement.exception.NoSuchAnnouncementException;
import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/announcement")
@Tag(name = "공지사항 작성/수정/삭제", description = "공지사항 생성, 수정, 삭제 API")
public class AnnouncementCommandController {

    private final AnnouncementCommandService announcementCommandService;

    @PostMapping
    @Operation(summary = "공지사항 생성", description = "새로운 공지사항을 작성합니다.")
    public ResponseEntity<ApiResponse<AnnouncementCreateResponse>> createAnnouncement(
            @RequestBody @Valid AnnouncementCreateRequest announcementCreateRequest,
            @AuthenticationPrincipal UserDetails userDetails) {

        AnnouncementCreateResponse response =
                announcementCommandService.create(announcementCreateRequest, userDetails);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }

    @PutMapping("{announcementId}")
    @Operation(summary = "공지사항 수정", description = "기존 공지사항을 수정합니다.")
    public ResponseEntity<ApiResponse<AnnouncementModifyResponse>> modifyAnnouncement(
            @RequestBody @Validated AnnouncementModifyRequest announcementModifyRequest,
            @PathVariable("announcementId") Long announcementId,
            @AuthenticationPrincipal UserDetails userDetails) {

        AnnouncementModifyResponse response =
                announcementCommandService.modify(announcementModifyRequest, announcementId, userDetails);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("{announcementId}")
    @Operation(summary = "공지사항 삭제", description = "공지사항을 삭제합니다.")
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
    public ResponseEntity<ApiResponse<Void>> handleNoSuchAnnouncementException(NoSuchAnnouncementException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileUploadFailedException(FileUploadFailedException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}