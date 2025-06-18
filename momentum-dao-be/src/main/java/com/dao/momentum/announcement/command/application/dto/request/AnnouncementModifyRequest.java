package com.dao.momentum.announcement.command.application.dto.request;

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

    private List<Long> remainFileIdList; // 유지하고 싶은 기존 파일 ID
}
