package com.dao.momentum.announcement.command.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnnouncementCreateRequest {
    @NotBlank(message = "공지사항 제목은 비어 있을 수 없습니다.")
    private String title;

    @NotBlank(message = "공지사항 내용은 비어 있을 수 없습니다.")
    private String content;

    @Valid
    private List<AttachmentRequest> attachments; // S3에 미리 업로드된 파일 정보 전달
}
