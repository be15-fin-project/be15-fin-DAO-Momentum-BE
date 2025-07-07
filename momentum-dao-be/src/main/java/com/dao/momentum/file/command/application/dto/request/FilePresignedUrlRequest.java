package com.dao.momentum.file.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "S3 Presigned URL 요청 정보")
public record FilePresignedUrlRequest(

        @Schema(description = "파일 이름", example = "업로드파일.pdf")
        String fileName,

        @Schema(description = "파일 크기 (바이트)", example = "1048576")
        long sizeInBytes,

        @Schema(description = "파일 MIME 타입", example = "application/pdf")
        String contentType,

        @Schema(description = "S3 저장 경로 구분자 (예: announcement, contract, approve)", example = "announcement")
        String prefixType   // ← "announcement", "contract", "approve" 중 하나
) {}

