package com.dao.momentum.file.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "파일 다운로드 URL 응답")
public class DownloadUrlResponse {

    @Schema(description = "서명된 S3 다운로드 URL", example = "https://s3.amazonaws.com/bucket/key?X-Amz-Signature=...")
    private String signedUrl;

    @Schema(description = "파일 이름", example = "공지사항.pdf")
    private String fileName;
}
