package com.dao.momentum.file.command.application.service;

import com.dao.momentum.file.command.application.dto.response.DownloadUrlResponse;
import com.dao.momentum.common.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class FileService {
    private final S3Service s3Service;

    public DownloadUrlResponse generateDownloadUrl(String key) {
        String signedUrl = s3Service.generateCloudFrontSignedUrl(key, Duration.ofMinutes(5));

        return DownloadUrlResponse.builder()
                .signedUrl(signedUrl)
                .build();
    }
}
