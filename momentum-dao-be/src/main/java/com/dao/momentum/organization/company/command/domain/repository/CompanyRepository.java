package com.dao.momentum.organization.company.command.domain.repository;

import com.dao.momentum.organization.company.command.domain.aggregate.Company;

import java.util.Optional;

public interface CompanyRepository {
    Optional<Company> findById(int i);
}
