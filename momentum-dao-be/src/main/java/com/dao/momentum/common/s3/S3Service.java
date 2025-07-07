package com.dao.momentum.common.s3;

import com.dao.momentum.file.command.application.dto.response.FilePresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.cloudfront.CloudFrontUtilities;
import software.amazon.awssdk.services.cloudfront.model.CustomSignerRequest;
import software.amazon.awssdk.services.cloudfront.url.SignedUrl;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudFrontDomain;

    @Value("${cloud.aws.cloudfront.key-pair-id}")
    private String keyPairId;

    @Value("${cloud.aws.cloudfront.private-key-path}")
    private String privateKeyPath;

    /**
     * 업로드용 Presigned URL 발급
     */
    public FilePresignedUrlResponse generatePresignedUploadUrlWithKey(String key, String contentType) {

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(resolveContentTypeWithCharset(contentType, key))
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(objectRequest)
                .build();

        String presignedUrl = s3Presigner.presignPutObject(presignRequest).url().toString();

        return new FilePresignedUrlResponse(presignedUrl, key);
    }

    /**
     * 다운로드용 CloudFront Signed URL 생성
     */
    public String generateCloudFrontSignedUrl(String key, Duration duration) {
        try {
            // 1. 경로를 안전하게 인코딩 (한글, 공백, 특수문자 포함 처리)
            String encodedPath = new URIBuilder()
                    .setPathSegments(key.split("/")) // 디렉토리 구조 포함 safe 분리
                    .build()
                    .getRawPath(); // 인코딩된 path 반환

            // 2. CloudFront 전체 리소스 URL 생성
            String resourceUrl = "https://" + cloudFrontDomain + encodedPath;

            // 3. 시작 시간과 만료 시간 설정
            Instant now = Instant.now();
            Instant expiration = now.plus(duration);

            // 4. 개인 키 Path 로드 (PKCS#8 PEM)
            Path keyPath = Paths.get(privateKeyPath);

            // 5. CustomSignerRequest 구성
            CustomSignerRequest request = CustomSignerRequest.builder()
                    .resourceUrl(resourceUrl)
                    .privateKey(keyPath)
                    .keyPairId(keyPairId)
                    .activeDate(now)
                    .expirationDate(expiration)
                    .ipRange("0.0.0.0/0")
                    .build();

            // 6. Signed URL 생성
            CloudFrontUtilities utilities = CloudFrontUtilities.create();
            SignedUrl signedUrl = utilities.getSignedUrlWithCustomPolicy(request);

            return signedUrl.url();

        } catch (Exception e) {
            log.error("CloudFront Signed URL 생성 실패", e);
            throw new RuntimeException("CloudFront Signed URL 생성 실패", e);
        }
    }

    /**
     * 백엔드에서 직접 업로드할 때 사용
     */
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

    /**
     * S3 파일 삭제 - s3Key 기준
     */
    public void deleteFileFromS3(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    /**
     * S3 Key에 적합하도록 파일명 정제
     */
    public String sanitizeFilename(String filename) {
        if (filename == null) return "unnamed";
        return filename.replaceAll("[^a-zA-Z0-9가-힣._-]", "_");
    }

    /**
     * S3 Key → CloudFront URL 변환
     */
    private String getCloudFrontUrl(String key) {
        return cloudFrontDomain + "/" + key;
    }

    /**
     * 확장자 추출 (e.g. png, pdf)
     */
    public String extractFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return null;
        }
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private String resolveContentTypeWithCharset(String contentType, String key) {
        String lowerKey = key.toLowerCase();
        // 텍스트 기반 확장자는 charset 추가
        if (lowerKey.endsWith(".txt") || lowerKey.endsWith(".csv")) {
            if (!contentType.toLowerCase().contains("charset")) {
                return contentType + "; charset=UTF-8";
            }
        }
        return contentType;
    }

    private String extractFilenameFromKey(String key) {
        if (key == null || key.isEmpty()) return "unnamed";
        int lastSlash = key.lastIndexOf('/');
        return lastSlash != -1 ? key.substring(lastSlash + 1) : key;
    }
}
