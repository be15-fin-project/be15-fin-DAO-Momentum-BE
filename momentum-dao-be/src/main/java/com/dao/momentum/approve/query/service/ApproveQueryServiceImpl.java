package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.approve.exception.NotExistTabException;
import com.dao.momentum.approve.exception.NotFoundApproveException;
import com.dao.momentum.approve.query.dto.*;
import com.dao.momentum.approve.query.dto.approveTypeDTO.OvertimeDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.DraftApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.response.ApproveDetailResponse;
import com.dao.momentum.approve.query.dto.response.ApproveResponse;
import com.dao.momentum.approve.query.dto.response.DraftApproveResponse;
import com.dao.momentum.approve.query.mapper.ApproveDetailMapper;
import com.dao.momentum.approve.query.mapper.ApproveMapper;
import com.dao.momentum.approve.query.mapper.ReceivedApproveMapper;
import com.dao.momentum.approve.query.mapper.DraftApproveMapper;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApproveQueryServiceImpl implements ApproveQueryService {

    private final ReceivedApproveMapper receivedApproveMapper;
    private final DraftApproveMapper draftApproveMapper;
    private final ApproveDetailMapper approveDetailMapper;
    private final ApproveMapper approveMapper;

    /* 받은 결재 목록을 조회하는 메서드 */
    @Transactional(readOnly = true)
    @Override
    public ApproveResponse getReceivedApprove(
            ApproveListRequest approveListRequest, Long empId, PageRequest pageRequest
    ) {

        // 존재하지 않는 결재 탭을 입력하는 경우 에러 처리
        List<String> validTabs = List.of("ALL", "ATTENDANCE", "PROPOSAL", "RECEIPT", "CANCEL");

        if (!validTabs.contains(approveListRequest.getTab())) {
            throw new NotExistTabException(ErrorCode.NOT_EXIST_TAB);
        }

        // 멤버 존재 여부에 따른 에러 처리
        if(!receivedApproveMapper.existsByEmpId(empId)) {
            throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        // 받은 결재 문서 가져오기
        List<ApproveDTO> approveList
                = receivedApproveMapper.findReceivedApproval(approveListRequest, empId ,pageRequest);

        // 받은 결재 문서 개수 세기
        long total = receivedApproveMapper.countReceivedApproval(approveListRequest, empId);

        // 페이징 처리를 위한 부분
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


    /* 보낸 결재 목록을 조회하는 메서드 */
    @Transactional(readOnly = true)
    @Override
    public DraftApproveResponse getDraftApprove(
            DraftApproveListRequest draftApproveListRequest, Long empId, PageRequest pageRequest
    ) {

        // 존재하지 않는 결재 탭을 입력하는 경우 에러 처리
        List<String> validTabs = List.of("ALL", "ATTENDANCE", "PROPOSAL", "RECEIPT", "CANCEL");

        if (!validTabs.contains(draftApproveListRequest.getTab())) {
            throw new NotExistTabException(ErrorCode.NOT_EXIST_TAB);
        }

        // 멤버 존재 여부에 따른 에러 처리
        if(!draftApproveMapper.existsByEmpId(empId)) {
            throw new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }

        // 받은 결재 문서 가져오기
        List<DraftApproveDTO> draftApproveList
                = draftApproveMapper.findDraftApproval(draftApproveListRequest, empId ,pageRequest);

        // 받은 결재 문서 개수 세기
        long total = draftApproveMapper.countDraftApproval(draftApproveListRequest, empId);

        // 페이징 처리를 위한 부분
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();

        return DraftApproveResponse.builder()
                .draftApproveDTO(draftApproveList)
                .pagination(Pagination.builder()
                        .currentPage(page)
                        .totalPage((int)Math.ceil((double)total/size))
                        .totalItems(total)
                        .build())
                .build();
    }

    /* 결재 상세 목록 조회 하기 */
    @Transactional(readOnly = true)
    @Override
    public ApproveDetailResponse getApproveDetail(Long approveId) {
        ApproveDTO approveDTO = approveDetailMapper.getApproveDTO(approveId)
                .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_APPROVE));

        // 취소 결재인 경우
        if (approveDTO.getApproveType() == ApproveType.CANCEL) {
            return getCancelApproveDetail(approveDTO);
        }

        // 일반 결재인 경우 (취소 제외)
        return getNormalApproveDetail(approveDTO);
    }

    /* 취소 결재인 경우 */
    private ApproveDetailResponse getCancelApproveDetail(ApproveDTO cancelDTO) {
        // 취소 결재 ID
        Long cancelApproveId = cancelDTO.getApproveId();
        // 해당 결재의 부모 ID
        Long parentId = cancelDTO.getParentApproveId();

        // 1. 부모 결재 DTO 조회 하기
        ApproveDTO parentDTO = approveDetailMapper.getApproveDTO(parentId)
                .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_APPROVE));

        // 2. 결재 첨부 파일
        List<ApproveFileDTO> approveFileDTO = approveDetailMapper.getApproveFiles(parentId);

        // 3. 결재선, 결재선 목록
        List<ApproveLineDTO> lineList = approveDetailMapper.getApproveLines(cancelApproveId);
        List<ApproveRefDTO> refList = approveDetailMapper.getApproveRefs(cancelApproveId);

        // 4. 폼 정보 가져오기
        Object formDetail = resolveFormDetail(ApproveType.CANCEL,cancelApproveId);

        return buildApproveDetailResponse(cancelDTO, parentDTO, approveFileDTO, lineList, refList, formDetail);
    }


    @Transactional(readOnly = true)
    public EmployeeLeaderDto getEmployeeLeader(Long empId) {
        return approveMapper.findEmployeeLeader(empId);
    }

    /* 일반 결재인 경우 */
    private ApproveDetailResponse getNormalApproveDetail(ApproveDTO approveDTO) {
        // 결재 아이디
        Long approveId = approveDTO.getApproveId();
        // 결재 타입
        ApproveType approveType = approveDTO.getApproveType();

        // 1. 결재 첨부 파일
        List<ApproveFileDTO> approveFileDTO = approveDetailMapper.getApproveFiles(approveId);

        // 2. 결재선, 결재선 목록
        List<ApproveLineDTO> lineList = approveDetailMapper.getApproveLines(approveId);
        List<ApproveRefDTO> refList = approveDetailMapper.getApproveRefs(approveId);

        // 3. 폼 정보 가져오기
        Object formDetail = resolveFormDetail(approveType, approveId);

        // 먄약 초과 근무인 경우에는 시간 계산을 통해 연장, 야간, 휴일 근무 인지 저장하기
        if (approveType == ApproveType.OVERTIME && formDetail instanceof OvertimeDTO overtimeDTO) {
            overtimeDTO.classifyWorkTypes();
        }

        return buildApproveDetailResponse(approveDTO, null, approveFileDTO, lineList, refList, formDetail);
    }

    /* 공통된 응답인 경우*/
    private ApproveDetailResponse buildApproveDetailResponse(
            ApproveDTO approveDTO,
            ApproveDTO parentApproveDTO,
            List<ApproveFileDTO> approveFileDTO,
            List<ApproveLineDTO> lineList,
            List<ApproveRefDTO> refList,
            Object formDetail
    ) {
        List<ApproveLineGroupDTO> approveLineGroupDTOs = lineList.stream()
                .map(line -> ApproveLineGroupDTO.builder()
                        .approveLineDTO(line)
                        .approveLineListDTOs(
                                approveDetailMapper.getApproveLineList(line.getApproveLineId())
                        )
                        .build())
                .toList();

        return ApproveDetailResponse.builder()
                .approveDTO(approveDTO)
                .parentApproveDTO(parentApproveDTO)
                .approveFileDTO(approveFileDTO)
                .approveLineGroupDTO(approveLineGroupDTOs)
                .approveRefDTO(refList)
                .formDetail(formDetail)
                .build();
    }

    /* 폼 데이터 결정하기 */
    private Object resolveFormDetail(ApproveType approveType, Long approveId) {
        return switch (approveType) {
            case PROPOSAL -> approveDetailMapper.getProposalDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_PROPOSAL));
            case RECEIPT -> approveDetailMapper.getReceiptDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_RECEIPT));
            case BUSINESSTRIP -> approveDetailMapper.getBusinessTripDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_BUSINESS_TRIP));
            case OVERTIME -> approveDetailMapper.getOvertimeDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_OVERTIME));
            case REMOTEWORK -> approveDetailMapper.getRemoteWorkDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_OVERTIME));
            case VACATION -> approveDetailMapper.getVacationDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_VACATION));
            case WORKCORRECTION -> approveDetailMapper.getWorkCorrectionDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_WORK_CORRECTION));
            case CANCEL -> approveDetailMapper.getCancelDetail(approveId)
                    .orElseThrow(() -> new NotFoundApproveException(ErrorCode.NOT_EXIST_CANCEL));
        };
    }
}
