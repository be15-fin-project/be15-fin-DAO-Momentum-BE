package com.dao.momentum.approve.query.service;

import com.dao.momentum.approve.query.dto.response.DayoffResponse;
import com.dao.momentum.approve.query.dto.response.RefreshResponse;

public interface RemainingVacationQueryService {

    /* 잔여 연차 시간을 조회하는 메서드 */
    DayoffResponse getRemainingDayoffs(long empId);

    /* 잔여 리프레시 휴가 일수를 조회하는 메서드 */
    RefreshResponse getRemainingRefreshs(long empId);
}
