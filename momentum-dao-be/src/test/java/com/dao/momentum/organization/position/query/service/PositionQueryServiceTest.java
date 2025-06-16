package com.dao.momentum.organization.position.query.service;

import com.dao.momentum.organization.position.query.dto.response.PositionDto;
import com.dao.momentum.organization.position.query.dto.response.PositionsResponse;
import com.dao.momentum.organization.position.query.mapper.PositionMapper;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PositionQueryServiceTest {
    @Mock
    private PositionMapper positionMapper;

    @InjectMocks
    private PositionQueryService positionQueryService;

    public PositionQueryServiceTest() {
        MockitoAnnotations.openMocks(this); // 초기화
    }

    @Test
    void testGetPositions() {
        // given
        PositionDto position1 = new PositionDto();
        position1.setPositionId(1);
        position1.setName("Manager");
        position1.setLevel(1);

        PositionDto position2 = new PositionDto();
        position2.setPositionId(2);
        position2.setName("Staff");
        position2.setLevel(2);

        when(positionMapper.findPositions()).thenReturn(List.of(position1, position2));

        // when
        PositionsResponse response = positionQueryService.getPositions();

        // then
        assertNotNull(response);
        assertEquals(2, response.getPositionDtoList().size());
        assertEquals("Manager", response.getPositionDtoList().get(0).getName());

        // verify
        verify(positionMapper, times(1)).findPositions();
    }
}