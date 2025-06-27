package com.dao.momentum.approve.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
@Schema(description = "영수증 OCR API 사용 response")
public class ReceiptOcrResultResponse {
    
    @Schema(description = "가게 이름")
    private String storeName;

    @Schema(description = "총 금액")
    private Integer amount;

    @Schema(description = "가게 주소")
    private String address;

    @Schema(description = "사용 날짜")
    private LocalDate usedAt;

}
