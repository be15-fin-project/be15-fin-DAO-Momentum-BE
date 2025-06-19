package com.dao.momentum.organization.contract.query.mapper;

import com.dao.momentum.organization.contract.query.dto.request.ContractSearchDTO;
import com.dao.momentum.organization.contract.query.dto.response.ContractSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminContractMapper {
    List<ContractSummaryDTO> getContracts(@Param("request") ContractSearchDTO request);

    long countContracts(@Param("request") ContractSearchDTO request);
}
