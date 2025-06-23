package com.dao.momentum.organization.contract.command.infrastructure.repository;

import com.dao.momentum.organization.contract.command.domain.aggregate.Contract;
import com.dao.momentum.organization.contract.command.domain.repository.ContractRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaContractRepository extends ContractRepository, JpaRepository<Contract, Long> {
}
