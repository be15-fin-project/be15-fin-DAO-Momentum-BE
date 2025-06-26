package com.dao.momentum.retention.prospect.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "retention_round")
public class RetentionRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "round_id", nullable = false, updatable = false)
    private Integer roundId;

    @Column(name = "round_no", nullable = false)
    private Integer roundNo;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "month", nullable = false)
    private Integer month;

    public static RetentionRound create(int roundNo, int year, int month) {
        return new RetentionRound(null, roundNo, year, month);
    }

    public void updateRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }
}
