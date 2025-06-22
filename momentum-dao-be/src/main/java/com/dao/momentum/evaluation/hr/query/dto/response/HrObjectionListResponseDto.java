package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.common.dto.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 인사 평가 이의제기 목록 응답 DTO (개별 항목)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인사 평가 이의제기 목록 응답 DTO")
public class HrObjectionListResponseDto {

    @Schema(description = "이의제기 ID", example = "5001")
    private Long objectionId;

    @Schema(description = "이의제기 대상 사원 번호", example = "20240001")
    private String empNo;

    @Schema(description = "이의제기 대상 사원 이름", example = "김현우")
    private String employeeName;

    @Schema(description = "평가 회차 번호", example = "2")
    private Integer roundNo;

    @Schema(description = "이의제기 생성 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
    private String createdAt;

    @Schema(
            description = "처리 상태",
            implementation = Status.class
    )
    private Status status;

    @Schema(description = "평가 점수", example = "85")
    private Integer score;

    @Schema(description = "평가 등급", example = "A")
    private String grade;
}
