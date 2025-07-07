package com.dao.momentum.file.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "파일 업로드용 S3 Presigned URL 응답")
public record FilePresignedUrlResponse(

        @Schema(description = "Presigned URL (PUT 요청용)", example = "https://s3.amazonaws.com/bucket/key?X-Amz-Signature=...")
        String presignedUrl,

        @Schema(description = "S3 저장 키 (DB 저장용)", example = "announcement/1858ddb8-7d8f-4bcd-bda3-96b3aa661516/work.txt")
        String s3Key
) {}

