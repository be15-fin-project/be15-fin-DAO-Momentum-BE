package com.dao.momentum.approve.query.dto.response;

import com.dao.momentum.approve.query.dto.*;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ApproveDetailResponse {

    private ApproveDTO approveDTO;
    private ApproveDTO parentApproveDTO;
    private List<ApproveFileDTO> approveFileDTO;
    private List<ApproveLineGroupDTO> approveLineGroupDTO;
    private List<ApproveRefDTO> approveRefDTO;
    private Object formDetail; // 폼의 종류에 따라서 상세 정보가 달라짐

}
