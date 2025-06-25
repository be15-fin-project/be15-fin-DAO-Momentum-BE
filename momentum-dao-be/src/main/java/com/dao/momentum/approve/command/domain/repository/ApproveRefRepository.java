package com.dao.momentum.approve.command.domain.repository;

import com.dao.momentum.approve.command.domain.aggregate.ApproveRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApproveRefRepository extends JpaRepository<ApproveRef, Long> {

}
