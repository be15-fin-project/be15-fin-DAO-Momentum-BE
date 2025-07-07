package com.dao.momentum.announcement.command.application.dto.request;

import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "공지사항 생성 요청")
public class AnnouncementCreateRequest {

    @NotBlank(message = "공지사항 제목은 비어 있을 수 없습니다.")
    @Schema(description = "공지사항 제목", example = "시스템 점검 안내")
    private String title;

    @NotBlank(message = "공지사항 내용은 비어 있을 수 없습니다.")
    @Schema(description = "공지사항 내용", example = "금일 오후 10시부터 시스템 점검이 진행됩니다.")
    private String content;

    @Valid
    @Schema(description = "첨부 파일 리스트 (S3에 미리 업로드된 파일 정보)")
    private List<AttachmentRequest> attachments;
}
