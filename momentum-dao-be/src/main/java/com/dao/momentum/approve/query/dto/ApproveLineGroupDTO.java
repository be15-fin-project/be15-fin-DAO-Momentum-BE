package com.dao.momentum.approve.query.dto;

import lombok.*;

import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveLineGroupDTO {

    private ApproveLineDTO approveLineDTO;
    private List<ApproveLineListDTO> approveLineListDTOs;

}
