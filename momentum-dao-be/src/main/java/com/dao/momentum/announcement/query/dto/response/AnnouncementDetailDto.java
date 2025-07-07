package com.dao.momentum.announcement.query.dto.response;

import com.dao.momentum.file.command.application.dto.response.AttachmentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "공지사항 상세 DTO")
public class AnnouncementDetailDto {

    @Schema(description = "공지사항 ID", example = "1")
    private Long announcementId;

    @Schema(description = "작성자 사원 ID", example = "1001")
    private Long empId;

    @Schema(description = "작성자 이름", example = "홍길동")
    private String employeeName;

    @Schema(description = "부서 ID", example = "101")
    private Integer deptId;

    @Schema(description = "부서명", example = "개발팀")
    private String departmentName;

    @Schema(description = "직급명", example = "사원")
    private String positionName;

    @Schema(description = "공지사항 제목", example = "시스템 점검 안내")
    private String title;

    @Schema(description = "공지사항 내용", example = "시스템 점검이 7월 10일에 예정되어 있습니다.")
    private String content;

    @Schema(description = "작성일시", example = "2025-07-07T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시", example = "2025-07-07T12:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "첨부파일 목록")
    private List<AttachmentDto> attachments;
}
