package com.dao.momentum.file.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "첨부 파일 정보 요청")
public class AttachmentRequest {

    @NotBlank(message = "파일 이름은 비어 있을 수 없습니다.")
    @Schema(description = "파일 이름", example = "work.txt")
    private String name;

    @NotBlank(message = "파일 S3 Key는 비어 있을 수 없습니다.")
    @Schema(description = "S3 저장 경로 (Key)", example = "announcement/1858ddb8-7d8f-4bcd-bda3-96b3aa661516/work.txt")
    private String s3Key;

    @NotBlank(message = "파일 타입은 비어 있을 수 없습니다.")
    @Schema(description = "파일 MIME 타입", example = "text/plain")
    private String type;
}

