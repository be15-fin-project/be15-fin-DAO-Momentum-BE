package com.dao.momentum.organization.position.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.position.command.application.dto.request.PositionCreateRequest;
import com.dao.momentum.organization.position.command.application.dto.request.PositionUpdateRequest;
import com.dao.momentum.organization.position.command.application.dto.response.PositionCreateResponse;
import com.dao.momentum.organization.position.command.application.dto.response.PositionDeleteResponse;
import com.dao.momentum.organization.position.command.application.dto.response.PositionUpdateResponse;
import com.dao.momentum.organization.position.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import com.dao.momentum.organization.position.exception.PositionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PositionCommandService {
    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public PositionCreateResponse createPositions(PositionCreateRequest request) {
        if (positionRepository.existsByNameAndIsDeleted(request.getName(),IsDeleted.N)) {
            throw new PositionException(ErrorCode.POSITION_ALREADY_EXISTS);
        }

        Integer requestedLevel = request.getLevel();

        //직위 단계 범위검사
        validateLevelForCreate(requestedLevel);

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

    @Transactional
    public PositionUpdateResponse updatePosition(PositionUpdateRequest request) {
        Integer requestedLevel = request.getLevel();

        //직위 단계 범위검사
        validateLevelForUpdate(requestedLevel);

        //직위 존재 검사
        Position position = positionRepository.findByPositionId(request.getPositionId()).orElseThrow(
                () ->  new PositionException(ErrorCode.POSITION_NOT_FOUND)
        );

        //직위명 검사
        if(!position.getName().equals(request.getName()) && positionRepository.existsByNameAndIsDeleted(request.getName(),IsDeleted.N)){
            throw new PositionException(ErrorCode.POSITION_ALREADY_EXISTS);
        }

        //직위 단계 조정
        if(position.getLevel()>requestedLevel){
            positionRepository.incrementLevelsInRange(requestedLevel, position.getLevel()-1);
        }
        else if(position.getLevel() < requestedLevel){
            positionRepository.decrementLevelsInRange(position.getLevel()+1, requestedLevel);
        }

        position.setName(request.getName());

        position.setLevel(requestedLevel);

        return new PositionUpdateResponse(position.getPositionId(), "직위가 수정되었습니다.");
    }

    @Transactional
    public PositionDeleteResponse deletePosition(Integer positionId) {
        //직위 존재 검사
        Position position = positionRepository.findByPositionId(positionId).orElseThrow(
                () ->  new PositionException(ErrorCode.POSITION_NOT_FOUND)
        );

        //해당 직위인 사원 검사
        if(employeeRepository.existsByPositionId(positionId)){
            throw new PositionException(ErrorCode.POSITION_IN_USE);
        }

        //직위 단계 조정
        positionRepository.decrementLevelsGreater(position.getLevel());

        //soft delete
        positionRepository.deleteByPositionId(positionId);

        return new PositionDeleteResponse(positionId, "삭제를 완료했습니다.");
    }

    private void validateLevelForCreate(int requestLevel) {
        Integer maxLevel = positionRepository.findMaxLevel();
        if (maxLevel == null) maxLevel = 0;

        if (requestLevel < 1 || requestLevel > maxLevel + 1) {
            throw new PositionException(ErrorCode.INVALID_LEVEL);
        }
    }

    private void validateLevelForUpdate(int requestLevel) {
        Integer maxLevel = positionRepository.findMaxLevel();

        if (maxLevel == null || requestLevel < 1 || requestLevel > maxLevel) {
            throw new PositionException(ErrorCode.INVALID_LEVEL);
        }
    }
}
