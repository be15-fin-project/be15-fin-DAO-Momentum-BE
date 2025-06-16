package com.dao.momentum.approve.query.dto.response;

import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApproveResponse {

    private List<ApproveDTO> approveDTO;
    private Pagination pagination;

}
