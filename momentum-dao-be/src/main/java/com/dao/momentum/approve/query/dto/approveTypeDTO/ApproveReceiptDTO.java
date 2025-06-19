package com.dao.momentum.approve.query.dto.approveTypeDTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveReceiptDTO {

    private String receiptType;
    private String storeName;
    private Integer amount;
    private LocalDateTime usedAt;

}
