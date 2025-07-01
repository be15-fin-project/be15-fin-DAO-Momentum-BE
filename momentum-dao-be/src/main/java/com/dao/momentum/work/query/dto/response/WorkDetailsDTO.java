package com.dao.momentum.work.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkDetailsDTO {
    private int breakTime; // 휴게시간 (분 단위)

    private LocalDateTime startPushedAt;

    private LocalDateTime endPushedAt;

    private IsNormalWork isNormalWork;
}
