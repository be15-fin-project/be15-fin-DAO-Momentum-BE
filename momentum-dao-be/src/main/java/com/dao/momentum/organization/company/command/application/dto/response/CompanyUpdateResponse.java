package com.dao.momentum.organization.company.command.application.dto.response;

import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompanyUpdateResponse {
    CompanyUpdateDTO updatedDTO;
    String message;
}
