package com.dao.momentum.organization.company.command.infrastructure.repository;

import com.dao.momentum.organization.company.command.domain.aggregate.Company;
import com.dao.momentum.organization.company.command.domain.repository.CompanyRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaCompanyRepository extends CompanyRepository, JpaRepository<Company, Integer> {
}
