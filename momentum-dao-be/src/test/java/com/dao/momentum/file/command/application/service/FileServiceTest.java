package com.dao.momentum.file.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.common.s3.S3Service;
import com.dao.momentum.file.command.application.dto.request.FilePresignedUrlRequest;
import com.dao.momentum.file.command.application.dto.response.DownloadUrlResponse;
import com.dao.momentum.file.command.application.dto.response.FilePresignedUrlResponse;
import com.dao.momentum.file.exception.FileUploadFailedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private FileService fileService;

    @Test
    @DisplayName("Presigned URL 생성 성공")
    void generatePresignedUrl_success() {
        // given
        String fileName = "test.png";
        String contentType = "image/png";
        long size = 1024L;
        String prefixType = "announcement";

        FilePresignedUrlRequest request = new FilePresignedUrlRequest(fileName, size, contentType, prefixType);

        when(s3Service.extractFileExtension(fileName)).thenReturn("png");
        when(s3Service.sanitizeFilename(fileName)).thenReturn(fileName);
        when(s3Service.generatePresignedUploadUrlWithKey(any(), eq(contentType)))
                .thenReturn(new FilePresignedUrlResponse("https://presigned.url", "announcement/generated/key"));

        // when
        FilePresignedUrlResponse response = fileService.generatePresignedUrl(request);

        // then
        assertNotNull(response);
        assertEquals("announcement/generated/key", response.s3Key());
        assertEquals("https://presigned.url", response.presignedUrl());

        // verify
        verify(s3Service).extractFileExtension(fileName);
        verify(s3Service).sanitizeFilename(fileName);
        verify(s3Service).generatePresignedUploadUrlWithKey(startsWith("announcement/"), eq(contentType));
    }

    @Test
    @DisplayName("Presigned URL 생성 실패 - 용량 초과")
    void generatePresignedUrl_fail_sizeTooLarge() {
        // given
        FilePresignedUrlRequest request = new FilePresignedUrlRequest("test.png", 20 * 1024 * 1024L, "image/png", "announcement");

        // when
        FileUploadFailedException e = assertThrows(FileUploadFailedException.class, () ->
                fileService.generatePresignedUrl(request)
        );

        // then
        assertEquals(ErrorCode.FILE_TOO_LARGE, e.getErrorCode());

        // verify: 내부 호출 안 됨
        verify(s3Service, never()).extractFileExtension(any());
        verify(s3Service, never()).sanitizeFilename(any());
        verify(s3Service, never()).generatePresignedUploadUrlWithKey(any(), any());
    }

    @Test
    @DisplayName("Presigned URL 생성 실패 - 확장자 불일치")
    void generatePresignedUrl_fail_invalidExtension() {
        // given
        FilePresignedUrlRequest request = new FilePresignedUrlRequest("malicious.exe", 1024L , "application/octet-stream", "announcement");

        when(s3Service.extractFileExtension("malicious.exe")).thenReturn("exe");

        // when
        FileUploadFailedException e = assertThrows(FileUploadFailedException.class, () ->
                fileService.generatePresignedUrl(request)
        );

        // then
        assertEquals(ErrorCode.INVALID_FILE_EXTENSION, e.getErrorCode());

        // verify
        verify(s3Service).extractFileExtension("malicious.exe");
        verify(s3Service, never()).sanitizeFilename(any());
        verify(s3Service, never()).generatePresignedUploadUrlWithKey(any(), any());
    }

    @Test
    @DisplayName("CloudFront Signed URL 생성 성공")
    void generateDownloadUrl_success() {
        // given
        String key = "announcements/uuid/test.png";
        String expectedUrl = "https://cloudfront.domain/test.png";

        when(s3Service.generateCloudFrontSignedUrl(key, Duration.ofMinutes(5)))
                .thenReturn(expectedUrl);

        // when
        DownloadUrlResponse response = fileService.generateDownloadUrl(key);

        // then
        assertNotNull(response);
        assertEquals(expectedUrl, response.getSignedUrl());

        // verify
        verify(s3Service).generateCloudFrontSignedUrl(key, Duration.ofMinutes(5));
    }
}