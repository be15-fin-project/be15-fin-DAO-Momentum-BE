package com.dao.momentum.file.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.file.command.application.dto.request.FilePresignedUrlRequest;
import com.dao.momentum.file.command.application.dto.response.FilePresignedUrlResponse;
import com.dao.momentum.file.command.application.dto.response.DownloadUrlResponse;
import com.dao.momentum.common.service.S3Service;
import com.dao.momentum.file.exception.FileUploadFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final S3Service s3Service;

    public FilePresignedUrlResponse generatePresignedUrl(FilePresignedUrlRequest request) {
        final long MAX_SIZE = 10 * 1024 * 1024; // 10MB 제한
        if (request.sizeInBytes() > MAX_SIZE) {
            throw new FileUploadFailedException(ErrorCode.FILE_TOO_LARGE);
        }

        String extension = s3Service.extractFileExtension(request.fileName());
        if (extension == null || !List.of(
                "jpg", "jpeg", "png",   // 이미지
                "pdf", "docx", "txt",   // 문서
                "hwp", "hwpx",          // 한글
                "xlsx", "xls",          // 엑셀
                "pptx", "ppt"           // 파워포인트
        ).contains(extension)) {
            throw new FileUploadFailedException(ErrorCode.INVALID_FILE_EXTENSION);
        }

        String sanitizedFilename = s3Service.sanitizeFilename(request.fileName());
        String key = "announcements/" + UUID.randomUUID() + "/" + sanitizedFilename;

        return s3Service.generatePresignedUploadUrlWithKey(key, request.contentType());
    }

    public DownloadUrlResponse generateDownloadUrl(String key) {
        String signedUrl = s3Service.generateCloudFrontSignedUrl(key, Duration.ofMinutes(5));

        return DownloadUrlResponse.builder()
                .signedUrl(signedUrl)
                .build();
    }
}
