package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.LocalDate;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveReceiptDTO {

    private String receiptType;
    private String storeName;
    private String storeAddress;
    private Integer amount;
    private LocalDate usedAt;

}
