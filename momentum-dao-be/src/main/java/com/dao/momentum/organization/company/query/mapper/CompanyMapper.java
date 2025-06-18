package com.dao.momentum.organization.company.query.mapper;

import com.dao.momentum.organization.company.query.dto.response.CompanyInfoDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CompanyMapper {
    CompanyInfoDTO getCompanyInfo();
}
