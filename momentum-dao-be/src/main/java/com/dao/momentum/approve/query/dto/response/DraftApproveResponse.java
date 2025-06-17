package com.dao.momentum.approve.query.dto.response;

import com.dao.momentum.approve.query.dto.DraftApproveDTO;
import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DraftApproveResponse {

    private List<DraftApproveDTO> draftApproveDTO;
    private Pagination pagination;

}
