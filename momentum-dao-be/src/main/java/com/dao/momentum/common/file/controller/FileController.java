package com.dao.momentum.common.file.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.file.dto.request.DownloadUrlRequest;
import com.dao.momentum.common.file.dto.response.DownloadUrlResponse;
import com.dao.momentum.common.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("file")
public class FileController {
    private final FileService fileService;

    @PostMapping("/download-url")
    public ResponseEntity<ApiResponse<DownloadUrlResponse>> getDownloadUrl(@RequestBody DownloadUrlRequest request) {
        DownloadUrlResponse response = fileService.generateDownloadUrl(request.getKey());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
