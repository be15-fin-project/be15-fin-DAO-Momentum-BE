package com.dao.momentum.organization.position.query.service;

import com.dao.momentum.organization.position.query.dto.response.PositionDto;
import com.dao.momentum.organization.position.query.dto.response.PositionsResponse;
import com.dao.momentum.organization.position.query.mapper.PositionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PositionQueryService {
    private final PositionMapper positionMapper;
    public PositionsResponse getPositions() {
        List<PositionDto> positionDtoList = positionMapper.findPositions();
        return new PositionsResponse(positionDtoList);
    }
}
