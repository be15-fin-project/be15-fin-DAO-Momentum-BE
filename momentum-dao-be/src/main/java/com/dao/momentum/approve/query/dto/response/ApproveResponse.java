package com.dao.momentum.approve.query.dto.response;

import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "결재 목록 응답 객체")
public class ApproveResponse {

    @Schema(description = "결재 목록 데이터 DTO 리스트")
    private List<ApproveDTO> approveDTO;

    @Schema(description = "페이징 정보")
    private Pagination pagination;

}