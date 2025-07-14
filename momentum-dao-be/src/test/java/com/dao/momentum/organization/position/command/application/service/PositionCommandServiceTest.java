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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionCommandServiceTest {
    private PositionRepository positionRepository;
    private EmployeeRepository employeeRepository;
    private ModelMapper modelMapper;
    private PositionCommandService positionCommandService;

    @BeforeEach
    void setUp() {
        positionRepository = mock(PositionRepository.class);
        employeeRepository = mock(EmployeeRepository.class);
        modelMapper = new ModelMapper();
        positionCommandService = new PositionCommandService(positionRepository, employeeRepository,modelMapper);
    }

    /*
    직위 생성 테스트
     */

    //직위가_이미_존재하면_예외를_던진다
    @Test
    void shouldThrowException_whenPositionNameAlreadyExists() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Manager", 1);

        when(positionRepository.existsByNameAndIsDeleted("Manager",IsDeleted.N)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> positionCommandService.createPositions(request))
                .isInstanceOf(PositionException.class)
                .hasMessageContaining(ErrorCode.POSITION_ALREADY_EXISTS.getMessage());

        verify(positionRepository, never()).save(any());
    }

    //동일한_level이_존재하면_이후_level들을_증가시키고_저장한다
    @Test
    void shouldIncrementLowerLevelsAndSave_whenLevelAlreadyExists() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Team Lead", 1);

        when(positionRepository.existsByName("Team Lead")).thenReturn(false);
        when(positionRepository.existsByLevel(1)).thenReturn(true);

        // when
        PositionCreateResponse response = positionCommandService.createPositions(request);

        // then
        verify(positionRepository).incrementLevelsGreaterThanEqual(1);
        verify(positionRepository).save(any(Position.class));

        assertThat(response.getMessage()).isEqualTo("직위가 생성되었습니다.");
    }

    //동일한_level이_없으면_그대로_저장한다
    @Test
    void shouldSaveDirectly_whenLevelDoesNotExist() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("CEO", 10);

        when(positionRepository.existsByName("CEO")).thenReturn(false);
        when(positionRepository.existsByLevel(10)).thenReturn(false);
        when(positionRepository.findMaxLevel()).thenReturn(9);

        // when
        PositionCreateResponse response = positionCommandService.createPositions(request);

        // then
        verify(positionRepository, never()).incrementLevelsGreaterThanEqual(anyInt());
        verify(positionRepository).save(any(Position.class));

        assertThat(response.getMessage()).isEqualTo("직위가 생성되었습니다.");
    }

    /*
    직위 수정 테스트
     */

    //직위 수정시 정상 수정
    @Test
    void shouldUpdatePosition_WhenInputIsValid() {
        // given
        Integer positionId = 1;
        Position originalPosition = new Position(positionId, "Developer", 3, IsDeleted.N);
        PositionUpdateRequest request = new PositionUpdateRequest(positionId, "Team Leader", 2);

        when(positionRepository.findByPositionId(positionId)).thenReturn(Optional.of(originalPosition));
        when(positionRepository.existsByName("Team Leader")).thenReturn(false);
        when(positionRepository.findMaxLevel()).thenReturn(5);

        // when
        PositionUpdateResponse response = positionCommandService.updatePosition(request);

        // then
        assertEquals(positionId, response.getPositionId());
        assertEquals("직위가 수정되었습니다.", response.getMessage());
        assertEquals("Team Leader", originalPosition.getName());
        assertEquals(2, originalPosition.getLevel());

        verify(positionRepository).incrementLevelsInRange(2, 2);
    }

    //직위가 없을 시 예외처리
    @Test
    void shouldThrowException_WhenPositionNotFound() {
        // given
        Integer positionId = 11;
        PositionUpdateRequest request = new PositionUpdateRequest(positionId, "Team Leader", 2);

        when(positionRepository.findMaxLevel()).thenReturn(10);
        when(positionRepository.findByPositionId(positionId)).thenReturn(Optional.empty());

        // then
        PositionException exception = assertThrows(PositionException.class, () ->
                positionCommandService.updatePosition(request));

        assertEquals(ErrorCode.POSITION_NOT_FOUND, exception.getErrorCode());
    }

    //이름이 이미 존재한다면 예외처리
    @Test
    void shouldThrowException_WhenNameAlreadyExists() {
        // given
        Integer positionId = 1;
        Position originalPosition = new Position(positionId, "Developer", 3, IsDeleted.N);
        PositionUpdateRequest request = new PositionUpdateRequest(positionId, "Manager", 3);

        when(positionRepository.findByPositionId(positionId)).thenReturn(Optional.of(originalPosition));
        when(positionRepository.existsByNameAndIsDeleted("Manager",IsDeleted.N)).thenReturn(true);
        when(positionRepository.findMaxLevel()).thenReturn(5);

        // then
        PositionException exception = assertThrows(PositionException.class, () ->
                positionCommandService.updatePosition(request));

        assertEquals(ErrorCode.POSITION_ALREADY_EXISTS, exception.getErrorCode());
    }

    //직위가 범위를 벗어났다면 예외처리
    @Test
    void shouldThrowException_WhenRequestedLevelOutOfRange() {
        // given
        Integer positionId = 1;
        PositionUpdateRequest request = new PositionUpdateRequest(positionId, "Developer", 10);

        when(positionRepository.findMaxLevel()).thenReturn(5);

        // then
        PositionException exception = assertThrows(PositionException.class, () ->
                positionCommandService.updatePosition(request));

        assertEquals(ErrorCode.INVALID_LEVEL, exception.getErrorCode());
    }

    /*
    직위 삭제 테스트
     */

    //직위에 해당하는 사원이 없다면 정상 삭제
    @Test
    void shouldDeletePositionSuccessfully_whenPositionExistsAndNotInUse() {
        // Given
        Integer positionId = 1;
        Position position = Position.builder()
                .positionId(positionId)
                .name("Manager")
                .level(3)
                .build();

        when(positionRepository.findByPositionId(positionId))
                .thenReturn(Optional.of(position));

        when(employeeRepository.existsByPositionId(positionId))
                .thenReturn(false);

        // When
        PositionDeleteResponse response = positionCommandService.deletePosition(positionId);

        // Then
        verify(positionRepository).decrementLevelsGreater(position.getLevel());
        verify(positionRepository).deleteByPositionId(positionId);

        Assertions.assertEquals(positionId, response.getPositionId());
        Assertions.assertEquals("삭제를 완료했습니다.", response.getMessage());
    }

    //해당 직위가 없다면 예외처리
    @Test
    void shouldThrowException_whenPositionNotFound() {
        // Given
        Integer positionId = 1;
        when(positionRepository.findByPositionId(positionId))
                .thenReturn(Optional.empty());

        // When & Then
        PositionException ex = Assertions.assertThrows(PositionException.class, () ->
                positionCommandService.deletePosition(positionId));

        Assertions.assertEquals(ErrorCode.POSITION_NOT_FOUND, ex.getErrorCode());
    }

    //해당 직위인 사원이 있다면 예외처리
    @Test
    void shouldThrowException_whenPositionIsInUse() {
        // Given
        Integer positionId = 1;
        Position position = Position.builder()
                .positionId(positionId)
                .name("Manager")
                .level(3)
                .build();

        when(positionRepository.findByPositionId(positionId))
                .thenReturn(Optional.of(position));
        when(employeeRepository.existsByPositionId(positionId))
                .thenReturn(true);

        // When & Then
        PositionException ex = Assertions.assertThrows(PositionException.class, () ->
                positionCommandService.deletePosition(positionId));

        Assertions.assertEquals(ErrorCode.POSITION_IN_USE, ex.getErrorCode());
    }
}