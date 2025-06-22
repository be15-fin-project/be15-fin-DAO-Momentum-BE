package com.dao.momentum.evaluation.hr.query.controller;

import com.dao.momentum.evaluation.hr.query.dto.request.MyHrEvaluationListRequestDto;
import com.dao.momentum.evaluation.hr.query.dto.response.HrEvaluationListResultDto;
import com.dao.momentum.evaluation.hr.query.service.EvaluationHrService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/evaluations/hr")
@RequiredArgsConstructor
@Validated
public class EvaluationHrController {

    private final EvaluationHrService evaluationHrService;

    @GetMapping
    public HrEvaluationListResultDto getMyHrEvaluations(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute MyHrEvaluationListRequestDto req
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());

        return evaluationHrService.getHrEvaluations(empId, req);
    }
}
