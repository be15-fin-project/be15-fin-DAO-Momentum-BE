package com.dao.momentum.file.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attachmentId;

    private Long announcementId;

    private Long approveId;

    private Long contractId;

    @NotBlank
    private String name;

    @NotBlank
    @Column(name = "s3_key")
    private String s3Key;

    @NotBlank
    private String type;
}
