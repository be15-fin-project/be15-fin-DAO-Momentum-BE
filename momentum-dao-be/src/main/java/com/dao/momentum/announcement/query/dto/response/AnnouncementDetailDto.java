package com.dao.momentum.announcement.query.dto.response;

import com.dao.momentum.file.command.application.dto.response.AttachmentDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnouncementDetailDto {
    private Long announcementId;
    private Long empId;
    private String employeeName;
    private Integer deptId;
    private String departmentName;
    private String positionName;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<AttachmentDto> attachments; // 첨부파일 URL 목록
}
