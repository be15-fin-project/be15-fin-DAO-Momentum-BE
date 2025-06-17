package com.dao.momentum.approve.query.mapper;

import com.dao.momentum.approve.query.dto.ApproveDTO;
import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApproveMapper {

    List<ApproveDTO> findReceivedApproval(
            @Param("req") ApproveListRequest approveListRequest,
            @Param("empId") Long empId,
            @Param("pageRequest") PageRequest pageRequest
    );

    long countReceivedApproval(
            @Param("req") ApproveListRequest approveListRequest,
            @Param("empId") Long empId
    );

    boolean existsByEmpId(Long empId);

}

                            