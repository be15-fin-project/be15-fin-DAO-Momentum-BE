package com.dao.momentum.organization.company.command.infrastructure.repository;

import com.dao.momentum.organization.company.command.domain.aggregate.IpAddress;
import com.dao.momentum.organization.company.command.domain.repository.IpAddressRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaIpAddressRepository extends IpAddressRepository, JpaRepository<IpAddress, Integer> {
}
