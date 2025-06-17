package com.dao.momentum.organization.company.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateDTO;
import com.dao.momentum.organization.company.command.application.dto.request.CompanyUpdateRequest;
import com.dao.momentum.organization.company.command.application.dto.response.CompanyUpdateResponse;
import com.dao.momentum.organization.company.command.domain.aggregate.Company;
import com.dao.momentum.organization.company.command.domain.repository.CompanyRepository;
import com.dao.momentum.organization.company.exception.CompanyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyCommandServiceTest {
    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyCommandService companyCommandService;

    private CompanyUpdateRequest createCompanyUpdateRequest() {
        return new CompanyUpdateRequest(
                1,
                "New Company",
                "New Chairman",
                "New Address",
                "010-1234-5678",
                "999-88-77777",
                "new@example.com",
                10,
                LocalDate.of(2020, 1, 1),
                LocalTime.of(9, 0)
        );
    }

    @Test
    @DisplayName("회사 정보 수정 성공")
    void updateCompany_success() {
        CompanyUpdateRequest request = createCompanyUpdateRequest();
        // given
        Company company = mock(Company.class);
        when(companyRepository.findById(1)).thenReturn(Optional.of(company));

        // when
        CompanyUpdateResponse response = companyCommandService.updateCompany(request);

        // then
        assertThat(response.getMessage()).isEqualTo("회사 정보를 변경했습니다.");
        assertThat(response.getUpdatedDTO().getName()).isEqualTo("New Company");

        verify(company).updateFrom(any(CompanyUpdateDTO.class));
    }

    @Test
    @DisplayName("회사 정보 수정 실패 - 회사 조회 실패")
    void updateCompanyFail_whenCompanyNotFound() {
        CompanyUpdateRequest request = createCompanyUpdateRequest();
        // given
        when(companyRepository.findById(1)).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> companyCommandService.updateCompany(request))
                .isInstanceOf(CompanyException.class)
                .hasMessageContaining(ErrorCode.COMPANY_INFO_NOT_FOUND.getMessage());
    }
}