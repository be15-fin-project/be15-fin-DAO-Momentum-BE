package com.dao.momentum.approve.query.dto.response;

import com.dao.momentum.approve.query.dto.DraftApproveDTO;
import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "보낸 결재 목록 응답 객체")
public class DraftApproveResponse {

    @Schema(description = "보낸 결재 목록 DTO 리스트")
    private List<DraftApproveDTO> draftApproveDTO;

    @Schema(description = "페이징 정보")
    private Pagination pagination;

}