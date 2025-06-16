package com.dao.momentum.organization.position.command.application.service;

import com.dao.momentum.organization.position.command.application.dto.request.PositionCreateRequest;
import com.dao.momentum.organization.position.command.application.dto.response.PositionCreateResponse;
import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PositionCommandService {
    private final PositionRepository positionRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PositionCreateResponse createPositions(PositionCreateRequest request) {
        if (positionRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("이미 존재하는 직위 이름입니다.");
        }
        Integer requestedLevel = request.getLevel();

        // 이미 존재하는 레벨인지 확인
        boolean levelExists = positionRepository.existsByLevel(requestedLevel);
        if (levelExists) {
            // 요청한 level 이상인 모든 position을 level 1씩 증가
            positionRepository.incrementLevelsGreaterThanEqual(requestedLevel);
        }

        Position position = modelMapper.map(request, Position.class);
        positionRepository.save(position);

        return new PositionCreateResponse(position.getPositionId(), "직위가 생성되었습니다.");
    }
}
