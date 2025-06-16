package com.dao.momentum.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    public String uploadFile(String key, InputStream inputStream, String contentType) throws IOException {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(contentType != null ? contentType : "application/octet-stream")
                        .build(),
                RequestBody.fromInputStream(inputStream, inputStream.available())
        );

        return getCloudFrontUrl(key);
    }

    public void deleteFile(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    public void deleteFileFromS3(String url) {
        String key = extractKeyFromUrl(url);
        deleteFile(key);
    }

    // cloudfront 도메인부분을 제거하고 S3 Key 추출
    public String extractKeyFromUrl(String url) {
        if (url == null || !url.startsWith(cloudFrontDomain)) {
            throw new IllegalArgumentException("Invalid CloudFront URL: " + url);
        }
        return url.substring(cloudFrontDomain.length() + 1);
    }

    public String sanitizeFilename(String filename) {
        if (filename == null) return "unnamed";

        // S3 key에 안전한 문자만 허용 (HTTP에서 URL은 기본적으로 ASCII 문자만 안전하게 표현 가능)
        return filename.replaceAll("[^a-zA-Z0-9가-힣._-]", "_");
    }


    public String getCloudFrontUrl(String key) {
        return cloudFrontDomain + "/" + key;
    }

    public String extractFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase(); // 예: png, pdf
    }
}



