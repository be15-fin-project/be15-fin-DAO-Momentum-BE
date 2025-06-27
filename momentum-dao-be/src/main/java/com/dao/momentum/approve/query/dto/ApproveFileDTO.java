package com.dao.momentum.approve.query.dto;

import lombok.*;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveFileDTO {

    private String s3Key;
    private String type;

}
