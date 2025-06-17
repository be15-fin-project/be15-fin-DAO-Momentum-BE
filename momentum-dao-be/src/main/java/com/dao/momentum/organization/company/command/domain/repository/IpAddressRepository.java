package com.dao.momentum.organization.company.command.domain.repository;

import com.dao.momentum.organization.company.command.domain.aggregate.IpAddress;

import java.util.List;

public interface IpAddressRepository {
    List<IpAddress> findAll();
}
