package com.dao.momentum.announcement.command.application.dto.request;

import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AnnouncementModifyRequest {
    @NotBlank(message = "공지사항 제목은 비어 있을 수 없습니다.")
    private String title;

    @NotBlank(message = "공지사항 내용은 비어 있을 수 없습니다.")
    private String content;

    @Valid
    private List<AttachmentRequest> attachments;

    private List<Long> remainFileIdList;
}

