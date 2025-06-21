package com.dao.momentum.organization.contract.query.mapper;

import com.dao.momentum.organization.contract.query.dto.request.AdminContractSearchDTO;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchDTO;
import com.dao.momentum.organization.contract.query.dto.response.AdminContractDTO;
import com.dao.momentum.organization.contract.query.dto.response.ContractDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ContractMapper {
    List<AdminContractDTO> getContracts(@Param("request") AdminContractSearchDTO request);

    long countContracts(@Param("request") AdminContractSearchDTO request);

    List<ContractDTO> getMyContracts(@Param("request") ContractSearchDTO request, long empId);

    long countMyContracts(@Param("request") ContractSearchDTO request , long empId);
}
