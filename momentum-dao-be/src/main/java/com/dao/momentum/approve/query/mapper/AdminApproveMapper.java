package com.dao.momentum.approve.query.mapper;

import com.dao.momentum.approve.query.dto.request.ApproveListRequest;
import com.dao.momentum.approve.query.dto.request.PageRequest;
import com.dao.momentum.approve.query.dto.ApproveDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminApproveMapper {

    List<ApproveDTO> findAllApproval(
            @Param("req") ApproveListRequest approveListRequest,
            @Param("pageRequest") PageRequest pageRequest
    );

    long countAllApproval(@Param("req") ApproveListRequest approveListRequest);

}

                            