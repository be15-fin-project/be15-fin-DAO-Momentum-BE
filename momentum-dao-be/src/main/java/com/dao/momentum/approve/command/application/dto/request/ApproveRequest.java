package com.dao.momentum.approve.command.application.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.file.command.application.dto.request.AttachmentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "결재 request")
public class ApproveRequest {

    @Schema(description = "상위 결재 ID", example = "1")
    private final Long parentApproveId;

    @NotBlank(message = "제목은 반드시 입력해야 합니다.")
    @Schema(description = "결재 제목")
    private final String approveTitle;

    @NotNull
    @Schema(description = "결재 종류")
    private final ApproveType approveType;

    // 결재선
    @Schema(description = "결재선 목록")
    private final List<ApproveLineRequest> approveLineLists;

    // 참조하는 사람
    @Schema(description = "참조 사원 목록")
    private final List<ApproveRefRequest> refRequests;

    // 결재 문서가 많아서 이것을 DTO로 통합하기 어려움
    // 그래서 json 형식으로 폼을 저장한 뒤에 나중에 DTO 객체로 변환해서 사용함
    @Schema(description = "결재 문서 내용")
    private final JsonNode formDetail;

    @Valid
    @Schema(description = "첨부파일 목록")
    private final List<AttachmentRequest> attachments; // S3에 미리 업로드된 파일 정보 전달
}
