package com.dao.momentum.retention.query.dto.request;

import lombok.Getter;
import lombok.Setter;

/**
 * mapper에서 직접 조회하는 점수 포함 raw DTO
 */
@Getter
@Setter
public class RetentionSupportRaw {
    private String empName;
    private String deptName;
    private String positionName;
    private int retentionScore;      // 점수 → 등급/유형 변환용
    private String summaryComment;
    private int roundNo;
}
