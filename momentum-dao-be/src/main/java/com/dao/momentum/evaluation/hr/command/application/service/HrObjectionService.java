package com.dao.momentum.evaluation.hr.command.application.service;

import com.dao.momentum.evaluation.hr.command.application.dto.request.HrObjectionCreateDto;
import com.dao.momentum.evaluation.hr.command.application.dto.response.HrObjectionCreateResponse;

public interface HrObjectionService {

    HrObjectionCreateResponse create(HrObjectionCreateDto dto);
}
