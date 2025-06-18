package com.dao.momentum.approve.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "approve_proposal")
public class ApproveProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "approve_proposal_id")
    private Long approveProposalId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "approve_id", nullable = false)
    private Long approveId;

    @Builder
    public ApproveProposal(String content, Long approveId) {
        this.content = content;
        this.approveId = approveId;
    }
}

