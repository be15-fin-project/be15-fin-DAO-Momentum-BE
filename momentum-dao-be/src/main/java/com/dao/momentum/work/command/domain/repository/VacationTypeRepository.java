package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.VacationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VacationTypeRepository extends JpaRepository<VacationType, Integer> {

    VacationType getVacationTypeByVacationTypeId(int vacationTypeId);

}
