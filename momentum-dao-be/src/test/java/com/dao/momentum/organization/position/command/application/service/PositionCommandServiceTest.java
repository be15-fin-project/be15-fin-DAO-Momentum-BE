package com.dao.momentum.organization.position.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.position.command.application.dto.request.PositionCreateRequest;
import com.dao.momentum.organization.position.command.application.dto.response.PositionCreateResponse;
import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import com.dao.momentum.organization.position.exception.PositionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionCommandServiceTest {
    private PositionRepository positionRepository;
    private ModelMapper modelMapper;
    private PositionCommandService positionCommandService;

    @BeforeEach
    void setUp() {
        positionRepository = mock(PositionRepository.class);
        modelMapper = new ModelMapper();
        positionCommandService = new PositionCommandService(positionRepository, modelMapper);
    }

    //직위가_이미_존재하면_예외를_던진다
    @Test
    void shouldThrowException_whenPositionNameAlreadyExists() {
        // given
        PositionCreateRequest request = new PositionCreateRequest("Manager", 1);

        when(positionRepository.existsByName("Manager")).thenReturn(true);

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

        // when
        PositionCreateResponse response = positionCommandService.createPositions(request);

        // then
        verify(positionRepository, never()).incrementLevelsGreaterThanEqual(anyInt());
        verify(positionRepository).save(any(Position.class));

        assertThat(response.getMessage()).isEqualTo("직위가 생성되었습니다.");
    }
}