package com.dao.momentum.approve.query.mapper;

import com.dao.momentum.approve.query.dto.*;
import com.dao.momentum.approve.query.dto.approveTypeDTO.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ApproveDetailMapper {

    /* 모든 폼에서 공통적으로 조회되는 내용 */
    Optional<ApproveDTO> getApproveDTO(Long approveId);

    List<ApproveFileDTO> getApproveFiles(Long approveId);

    List<ApproveLineDTO> getApproveLines(Long approveId);

    List<ApproveLineListDTO> getApproveLineList(Long approveLineId);

    List<ApproveRefDTO> getApproveRefs( Long approveId);

    /* 폼별로 상세 조회 */
    Optional<ApproveProposalDTO> getProposalDetail(Long approveId);

    Optional<ApproveReceiptDTO> getReceiptDetail(Long approveId);

    Optional<ApproveCancelDTO> getCancelDetail(Long approveId);

    Optional<BusinessTripDTO> getBusinessTripDetail(Long approveId);

    Optional<OvertimeDTO> getOvertimeDetail(Long approveId);

    Optional<RemoteWorkDTO> getRemoteWorkDetail(Long approveId);

    Optional<VacationDTO> getVacationDetail(Long approveId);

    Optional<WorkCorrectionDTO> getWorkCorrectionDetail(Long approveId);

}

                            