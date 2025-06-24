package com.dao.momentum.approve.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class ReceiptOcrResultResponse {

    private String storeName;
    private Integer amount;
    private String address;
    private LocalDate usedAt;

}
