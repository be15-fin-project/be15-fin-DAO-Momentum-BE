package com.dao.momentum.work.query.service;

import com.dao.momentum.work.query.dto.request.AdminWorkSearchRequest;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.WorkDTO;
import com.dao.momentum.work.query.dto.response.WorkListResponse;
import com.dao.momentum.work.query.mapper.WorkMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkQueryServiceTest {

    @InjectMocks
    private WorkQueryService workQueryService;

    @Mock
    private WorkMapper workMapper;

    @Test
    void getMyWorks_ShouldReturnWorkListResponse() {
        // given
        String empIdStr = "123";
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(empIdStr);

        WorkSearchRequest request = WorkSearchRequest.builder()
                .rangeStartDate(LocalDate.of(2024, 1, 1))
                .rangeEndDate(LocalDate.of(2024, 1, 10))
                .build();

        List<WorkDTO> mockWorks = List.of(
                WorkDTO.builder().workId(1L).empId(123L).build()
        );
        when(workMapper.getMyWorks(any(WorkSearchRequest.class), eq(123L))).thenReturn(mockWorks);

        // when
        WorkListResponse response = workQueryService.getMyWorks(userDetails, request);

        // then
        assertNotNull(response);
        assertEquals(1, response.getWorks().size());
        assertNull(response.getPagination());
    }

    @Test
    void getWorks_ShouldReturnWorkListResponseWithPagination() {
        // given
        AdminWorkSearchRequest request = AdminWorkSearchRequest.builder()
                .rangeStartDate(LocalDate.of(2024, 2, 1))
                .rangeEndDate(LocalDate.of(2024, 2, 10))
                .build();

        List<WorkDTO> mockWorks = IntStream.rangeClosed(1, 18)
                .mapToObj(i -> WorkDTO.builder().workId((long) i).empId(321L).build())
                .collect(Collectors.toList());

        when(workMapper.getWorks(any(AdminWorkSearchRequest.class))).thenReturn(mockWorks);
        when(workMapper.countWorks(any(AdminWorkSearchRequest.class))).thenReturn(18L);

        // when
        WorkListResponse response = workQueryService.getWorks(request);

        // then
        assertNotNull(response);
        assertEquals(18, response.getWorks().size());

        assertNotNull(response.getPagination());
        assertEquals(1, response.getPagination().getCurrentPage());
        assertEquals(18, response.getPagination().getTotalItems());
        assertEquals(2, response.getPagination().getTotalPage()); // 18 / 10 = 1.8 => ceil = 2
    }
}