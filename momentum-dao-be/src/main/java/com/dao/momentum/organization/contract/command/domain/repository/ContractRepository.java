package com.dao.momentum.organization.contract.command.domain.repository;

import com.dao.momentum.organization.contract.command.domain.aggregate.Contract;
import com.dao.momentum.organization.contract.command.domain.aggregate.ContractType;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ContractRepository {
    Contract save(Contract contract);

    Optional<Contract> findById(long contractId);

    void delete(Contract contractToDelete);

    Optional<Contract> findTop1ByEmpIdAndTypeOrderByCreatedAtDesc(long empId, ContractType contractType);

    Optional<Contract> findTop1ByEmpIdAndTypeAndCreatedAtAfterOrderByCreatedAtAsc(long empId, ContractType contractType, LocalDateTime cutoff);
}
