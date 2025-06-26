package com.dao.momentum.retention.prospect.query.service;

public interface RetentionSupportExcelDownloadService {
    byte[] downloadExcel(Long roundId, Long deptId, String stabilityType);
}
