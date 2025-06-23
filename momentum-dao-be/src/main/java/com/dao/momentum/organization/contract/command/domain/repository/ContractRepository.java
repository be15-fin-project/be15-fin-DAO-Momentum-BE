package com.dao.momentum.organization.contract.command.domain.repository;

import com.dao.momentum.organization.contract.command.domain.aggregate.Contract;

public interface ContractRepository {
    Contract save(Contract contract);
}
