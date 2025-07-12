package com.dao.momentum.evaluation.kpi.query.service;

public interface KpiRetentionService {
    int calculateTotalKpiPenalty(Long empId, Integer year, Integer month);
}
