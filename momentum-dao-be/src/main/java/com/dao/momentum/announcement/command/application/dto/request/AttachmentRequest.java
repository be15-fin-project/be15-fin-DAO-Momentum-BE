package com.dao.momentum.announcement.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentRequest {
    @NotBlank(message = "파일 S3 Key는 비어 있을 수 없습니다.")
    private String s3Key;

    @NotBlank(message = "파일 타입은 비어 있을 수 없습니다.")
    private String type;
}
