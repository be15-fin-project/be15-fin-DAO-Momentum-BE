package com.dao.momentum.approve.command.application.dto.request.approveType;

import com.dao.momentum.approve.command.domain.aggregate.ReceiptType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@RequiredArgsConstructor
@Schema(description = "영수증 결재 request")
public class ApproveReceiptRequest {

    @NotNull(message="영수증 종류는 반드시 선택해야 합니다.")
    @Schema(description = "영수증 종류")
    private final ReceiptType receiptType;

    @NotBlank(message = "가게 이름은 반드시 작성해야 합니다.")
    @Schema(description = "가게 이름")
    private final String storeName;

    @Schema(description = "총 금액")
    private final int amount;

    @NotBlank(message="가게 주소는 반드시 작성해야 합니다.")
    @Schema(description = "가게 주소")
    private final String address;

    @NotNull(message="사용한 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "사용 날짜")
    private final LocalDate usedAt;

}
