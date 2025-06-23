package com.dao.momentum.file.command.application.controller;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.file.command.application.dto.request.FilePresignedUrlRequest;
import com.dao.momentum.file.command.application.dto.response.FilePresignedUrlResponse;
import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.file.command.application.dto.request.DownloadUrlRequest;
import com.dao.momentum.file.command.application.dto.response.DownloadUrlResponse;
import com.dao.momentum.file.command.application.service.FileService;
import com.dao.momentum.file.exception.FileUploadFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("file")
public class FileController {
    private final FileService fileService;

    @PostMapping("/presigned-url")
    public ResponseEntity<ApiResponse<FilePresignedUrlResponse>> generatePresignedUrl(@RequestBody FilePresignedUrlRequest request) {
        FilePresignedUrlResponse response =
                fileService.generatePresignedUrl(request);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/download-url")
    public ResponseEntity<ApiResponse<DownloadUrlResponse>> getDownloadUrl(@RequestBody DownloadUrlRequest request) {
        DownloadUrlResponse response = fileService.generateDownloadUrl(request.getKey());

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileUploadFailedException(FileUploadFailedException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.failure(errorCode.getCode(), errorCode.getMessage()));
    }
}
