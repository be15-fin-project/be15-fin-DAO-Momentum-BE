package com.dao.momentum.retention.prospect.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionForecastCreateRequest;
import com.dao.momentum.retention.prospect.command.application.dto.response.RetentionForecastCreateResponse;
import com.dao.momentum.retention.prospect.command.application.facade.RetentionForecastFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/retention-forecasts")
@RequiredArgsConstructor
public class RetentionForecastController {

    private final RetentionForecastFacade retentionForecastFacade;

    @PostMapping
    public ApiResponse<RetentionForecastCreateResponse> createForecastRound(
            @RequestBody @Valid RetentionForecastCreateRequest request
    ) {
        RetentionForecastCreateResponse response = retentionForecastFacade.createNewForecast(request);
        return ApiResponse.success(response);
    }
}
