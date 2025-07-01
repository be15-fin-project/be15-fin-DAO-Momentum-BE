package com.dao.momentum.approve.query.dto.response;

import com.dao.momentum.approve.query.dto.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "결재 상세 응답 객체")
public class ApproveDetailResponse {

    @Schema(description = "결재 정보 DTO")
    private ApproveDTO approveDTO;

    @Schema(description = "부모 결재 정보 DTO")
    private ApproveDTO parentApproveDTO;

    @Schema(description = "결재에 첨부된 파일 목록 DTO")
    private List<ApproveFileDTO> approveFileDTO;

    @Schema(description = "결재 라인 그룹 목록 DTO")
    private List<ApproveLineGroupDTO> approveLineGroupDTO;

    @Schema(description = "결재 참조자 목록 DTO")
    private List<ApproveRefDTO> approveRefDTO;

    @Schema(description = "폼 상세 정보 (폼 종류에 따라 내용이 달라짐)")
    private Object formDetail;

}
