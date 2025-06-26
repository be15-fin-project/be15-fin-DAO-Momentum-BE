package com.dao.momentum.retention.prospect.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionForecastCreateRequest;
import com.dao.momentum.retention.prospect.command.application.dto.response.RetentionForecastCreateResponse;
import com.dao.momentum.retention.prospect.command.application.facade.RetentionForecastFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/retention-forecasts")
@RequiredArgsConstructor
@Tag(name = "Retention Forecast", description = "근속 전망 회차 생성 API")
public class RetentionForecastController {

    private final RetentionForecastFacade retentionForecastFacade;

    @PostMapping
    @Operation(
            summary = "근속 전망 회차 생성",
            description = "입력한 연도와 월을 기준으로 새로운 근속 전망 회차를 생성합니다."
    )
    public ApiResponse<RetentionForecastCreateResponse> createForecastRound(
            @RequestBody @Valid RetentionForecastCreateRequest request
    ) {
        RetentionForecastCreateResponse response = retentionForecastFacade.createNewForecast(request);
        return ApiResponse.success(response);
    }
}
