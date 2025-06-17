package com.dao.momentum.organization.company.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateDTO;
import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateRequest;
import com.dao.momentum.organization.company.command.application.dto.response.CompanyUpdateResponse;
import com.dao.momentum.organization.company.command.domain.aggregate.Company;
import com.dao.momentum.organization.company.command.domain.repository.CompanyRepository;
import com.dao.momentum.organization.company.exception.CompanyException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyCommandService {
    private final CompanyRepository companyRepository;

    @Transactional
    public CompanyUpdateResponse updateCompany(CompanyUpdateRequest companyUpdateRequest) {
        //회사 조회
        Company company = companyRepository.findById(companyUpdateRequest.getCompanyId()).orElseThrow(
                () -> new CompanyException(ErrorCode.COMPANY_INFO_NOT_FOUND)
        );

        //회사 정보 변경
        CompanyUpdateDTO dto = companyUpdateRequest.toDTO();
        company.updateFrom(dto);

        return new CompanyUpdateResponse(dto,"회사 정보를 변경했습니다.");
    }
}
