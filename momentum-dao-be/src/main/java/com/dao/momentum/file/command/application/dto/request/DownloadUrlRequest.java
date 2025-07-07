package com.dao.momentum.file.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파일 다운로드 URL 요청")
public class DownloadUrlRequest {

    @NotBlank
    @Schema(description = "S3 파일 키 (경로 포함)", example = "announcement/1858ddb8-7d8f-4bcd-bda3-96b3aa661516/work.txt")
    private String key;

    @NotBlank
    @Schema(description = "다운로드 시 사용자에게 보여줄 파일 이름", example = "공지사항.pdf")
    private String fileName;
}
