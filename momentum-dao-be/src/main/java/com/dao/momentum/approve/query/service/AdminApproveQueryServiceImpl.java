package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.exception.NotExistTabException;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.mapper.AdminApproveMapper;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminApproveQueryServiceImpl implements AdminApproveQueryService {

    private final AdminApproveMapper adminApproveMapper;

    /* 결재 전체 목록을 조회하는 메소드 */
    @Transactional(readOnly = true)
    public ApproveResponse getApproveList(
            ApproveListRequest approveListRequest, PageRequest pageRequest
    ) {

        List<String> validTabs = List.of("ALL", "ATTENDANCE", "PROPOSAL", "RECEIPT", "CANCEL");

        if (!validTabs.contains(approveListRequest.getTab())) {
            throw new NotExistTabException(ErrorCode.NOT_EXIST_TAB);
        }

        List<ApproveDTO> approveList
                = adminApproveMapper.findAllApproval(approveListRequest, pageRequest);

        long total = adminApproveMapper.countAllApproval(approveListRequest);

        int page = pageRequest.getPage();
        int size = pageRequest.getSize();

        return ApproveResponse.builder()
                .approveDTO(approveList)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int)Math.ceil((double)total/size))
                        .totalItems(total)
                        .build())
                .build();
    }

}
