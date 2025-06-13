package com.dao.momentum.approve.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approve_receipt")
public class ApproveReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_receipt_id")
    private Long approveReceiptId;

    @Column(name = "approve_id", nullable = false)
    private Long approveId;

    @Enumerated(EnumType.STRING)
    @Column(name = "receipt_type", nullable = false)
    private ReceiptType receiptType;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "used_at", nullable = false)
    private LocalDateTime usedAt;

    @Builder
    public ApproveReceipt(Long approveId, ReceiptType receiptType, String storeName,
                          Integer amount, LocalDateTime usedAt) {
        this.receiptType = receiptType;
        this.approveId = approveId;
        this.storeName = storeName;
        this.amount = amount;
        this.usedAt = usedAt;
    }
}
