package com.dao.momentum.organization.company.query.service;

import com.dao.momentum.organization.company.query.dto.response.CompanyGetResponse;
import com.dao.momentum.organization.company.query.dto.response.CompanyInfoDTO;
import com.dao.momentum.organization.company.query.mapper.CompanyMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyQueryService {
    private final CompanyMapper companyMapper;

    public CompanyGetResponse getCompany() {
        CompanyInfoDTO companyInfoDTO = companyMapper.getCompanyInfo();

        return new CompanyGetResponse(companyInfoDTO);
    }
}
