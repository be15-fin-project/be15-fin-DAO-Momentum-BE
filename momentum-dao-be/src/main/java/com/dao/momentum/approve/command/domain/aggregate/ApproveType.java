package com.dao.momentum.approve.command.domain.aggregate;

import com.dao.momentum.approve.exception.NotExistTabException;
import com.dao.momentum.common.exception.ErrorCode;

import java.util.Arrays;

public enum ApproveType {
    WORKCORRECTION, VACATION, BUSINESSTRIP, REMOTEWORK, OVERTIME, RECEIPT, PROPOSAL, CANCEL;

    // 탭이 있는지 검증
    public static ApproveType from(String name) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NotExistTabException(ErrorCode.NOT_EXIST_TAB));
    }
}
