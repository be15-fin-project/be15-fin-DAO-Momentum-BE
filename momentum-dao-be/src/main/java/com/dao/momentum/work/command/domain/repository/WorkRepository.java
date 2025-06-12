package com.dao.momentum.work.command.domain.repository;

import com.dao.momentum.work.command.domain.aggregate.Work;

public interface WorkRepository {
    Work save(Work work);
}
