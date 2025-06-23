package com.dao.momentum.organization.contract.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.organization.contract.query.dto.request.AdminContractSearchDTO;
import com.dao.momentum.organization.contract.query.dto.request.AdminContractSearchRequest;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchDTO;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchRequest;
import com.dao.momentum.organization.contract.query.dto.response.AdminContractDTO;
import com.dao.momentum.organization.contract.query.dto.response.AdminContractListResponse;
import com.dao.momentum.organization.contract.query.dto.response.ContractDTO;
import com.dao.momentum.organization.contract.query.dto.response.ContractListResponse;
import com.dao.momentum.organization.contract.query.mapper.ContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractQueryService {
    private final ContractMapper contractMapper;

    public AdminContractListResponse getContracts(
            AdminContractSearchRequest contractSearchRequest
    ) {
        AdminContractSearchDTO adminContractSearchDTO = AdminContractSearchRequest.fromRequest(contractSearchRequest);
        List<AdminContractDTO> contracts = contractMapper.getContracts(adminContractSearchDTO);

        long totalItems = contractMapper.countContracts(adminContractSearchDTO);

        int size = adminContractSearchDTO.getSize();
        int currentPage = adminContractSearchDTO.getPage();

        int totalPage = (int) Math.ceil((double) totalItems / size);

        return AdminContractListResponse.builder()
                .contracts(contracts)
                .pagination(
                        Pagination.builder()
                                .currentPage(currentPage)
                                .totalPage(totalPage)
                                .totalItems(totalItems)
                                .build()
                )
                .build();
    }

    public ContractListResponse getMyContracts(
            UserDetails userDetails, ContractSearchRequest contractSearchRequest
    ) {
        long empId = Long.parseLong(userDetails.getUsername());
        ContractSearchDTO contractSearchDTO = ContractSearchRequest.fromRequest(contractSearchRequest);

        List<ContractDTO> contracts = contractMapper.getMyContracts(contractSearchDTO, empId);

        long totalItems = contractMapper.countMyContracts(contractSearchDTO, empId);

        int size = contractSearchDTO.getSize();
        int currentPage = contractSearchDTO.getPage();

        int totalPage = (int) Math.ceil((double) totalItems / size);

        return ContractListResponse.builder()
                .contracts(contracts)
                .pagination(
                        Pagination.builder()
                                .currentPage(currentPage)
                                .totalPage(totalPage)
                                .totalItems(totalItems)
                                .build()
                )
                .build();
    }
}
