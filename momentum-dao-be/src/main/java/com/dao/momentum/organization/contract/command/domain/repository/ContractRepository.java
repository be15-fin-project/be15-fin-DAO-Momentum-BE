package com.dao.momentum.organization.contract.command.domain.repository;

import com.dao.momentum.organization.contract.command.domain.aggregate.Contract;

import java.util.Optional;

public interface ContractRepository {
    Contract save(Contract contract);

    Optional<Contract> findById(long contractId);

    void delete(Contract contractToDelete);
}
