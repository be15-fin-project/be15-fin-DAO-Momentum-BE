package com.dao.momentum.organization.contract.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchDTO;
import com.dao.momentum.organization.contract.query.dto.request.ContractSearchRequest;
import com.dao.momentum.organization.contract.query.dto.response.ContractListResponse;
import com.dao.momentum.organization.contract.query.dto.response.ContractSummaryDTO;
import com.dao.momentum.organization.contract.query.mapper.AdminContractMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminContractQueryService {
    private final AdminContractMapper adminContractMapper;

    public ContractListResponse getContracts(
            @ModelAttribute ContractSearchRequest contractSearchRequest
    ) {
        ContractSearchDTO contractSearchDTO = ContractSearchRequest.fromRequest(contractSearchRequest);
        List<ContractSummaryDTO> contracts = adminContractMapper.getContracts(contractSearchDTO);

        long totalItems = adminContractMapper.countContracts(contractSearchDTO);

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
