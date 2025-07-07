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
@Schema(description = "공지사항 수정 요청")
public class AnnouncementModifyRequest {

    @NotBlank(message = "공지사항 제목은 비어 있을 수 없습니다.")
    @Schema(description = "공지사항 제목", example = "시스템 점검 일정 변경")
    private String title;

    @NotBlank(message = "공지사항 내용은 비어 있을 수 없습니다.")
    @Schema(description = "공지사항 내용", example = "점검 일정이 금일 오후 11시로 변경되었습니다.")
    private String content;

    @Valid
    @Schema(description = "첨부 파일 리스트 (S3에 새로 업로드된 파일 정보)")
    private List<AttachmentRequest> attachments;

    @Schema(description = "유지할 기존 파일 ID 목록", example = "[1, 2, 3]")
    private List<Long> remainFileIdList;
}

