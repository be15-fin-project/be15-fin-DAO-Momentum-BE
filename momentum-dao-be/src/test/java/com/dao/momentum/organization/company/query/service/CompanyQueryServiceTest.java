package com.dao.momentum.organization.company.query.service;

import com.dao.momentum.organization.company.command.domain.aggregate.Company;
import com.dao.momentum.organization.company.query.dto.response.CompanyGetResponse;
import com.dao.momentum.organization.company.query.dto.response.CompanyInfoDTO;
import com.dao.momentum.organization.company.query.mapper.CompanyMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyQueryServiceTest {
    @Mock
    private  CompanyMapper companyMapper;

    @InjectMocks
    private CompanyQueryService companyQueryService;

    private Company company;

    @Test
    @DisplayName("회사 정보 조회")
    void getCompanyInfo(){
        CompanyInfoDTO mockDto = new CompanyInfoDTO(
                1,
                "Momentum Inc.",
                "Jane Doe",
                "123 Seoul Street",
                "010-1234-5678",
                "123-45-67890",
                "contact@momentum.com",
                25,
                LocalDate.of(2020, 1, 1),
                LocalDateTime.of(2024, 6, 17, 9, 0)
        );

        when(companyMapper.getCompanyInfo()).thenReturn(mockDto);

        // when
        CompanyGetResponse response = companyQueryService.getCompany();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCompanyInfoDTO().getName()).isEqualTo("Momentum Inc.");
        assertThat(response.getCompanyInfoDTO().getChairman()).isEqualTo("Jane Doe");

        verify(companyMapper, times(1)).getCompanyInfo();
    }

    @Test
    void getCompany_returnsNull_whenCompanyInfoIsMissing() {
        // given
        when(companyMapper.getCompanyInfo()).thenReturn(null);

        // when
        CompanyGetResponse response = companyQueryService.getCompany();

        // then
        assertThat(response).isNotNull();
        assertThat(response.getCompanyInfoDTO()).isNull();
    }

    @Test
    void getCompany_throwsException_whenMapperFails() {
        // given
        when(companyMapper.getCompanyInfo()).thenThrow(new RuntimeException("DB access error"));

        // when / then
        assertThatThrownBy(() -> companyQueryService.getCompany())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB access error");
    }
}