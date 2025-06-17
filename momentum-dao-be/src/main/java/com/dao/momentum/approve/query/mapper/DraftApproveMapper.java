package com.dao.momentum.approve.query.mapper;

import com.dao.momentum.approve.query.dto.DraftApproveDTO;
import com.dao.momentum.approve.query.dto.request.DraftApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DraftApproveMapper {

    List<DraftApproveDTO> findDraftApproval(
            @Param("req") DraftApproveListRequest draftApproveListRequest,
            @Param("empId") Long empId,
            @Param("pageRequest") PageRequest pageRequest
    );

    long countDraftApproval(
            @Param("req") DraftApproveListRequest draftApproveListRequest,
            @Param("empId") Long empId
    );

    boolean existsByEmpId(Long empId);

}

                            