package com.dao.momentum.work.command.application.service;

import com.dao.momentum.organization.company.command.domain.repository.IpAddressRepository;
import com.dao.momentum.work.command.application.dto.response.WorkEndResponse;
import com.dao.momentum.work.command.application.validator.IpValidator;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.application.validator.WorkUpdateValidator;
import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.aggregate.WorkType;
import com.dao.momentum.work.command.domain.aggregate.WorkTypeName;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import com.dao.momentum.work.command.domain.repository.WorkTypeRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkCommandServiceTest {

    @Mock
    private WorkRepository workRepository;

    @Mock
    private WorkTypeRepository workTypeRepository;

    @Mock
    private IpValidator ipValidator;

    @Mock
    private WorkCreateValidator workCreateValidator;

    @Mock
    private WorkUpdateValidator workUpdateValidator;

    @Mock
    private WorkTimeService workTimeService;

    @Mock
    private IpAddressRepository ipAddressRepository;

    @InjectMocks
    private WorkCommandService workCommandService;

    @Test
    void updateWork_shouldUpdateWorkAndReturnResponse() {
        // given
        long empId = 123L;
        LocalDateTime endPushedAt = LocalDateTime.of(2025, 6, 17, 17, 10, 30);
        LocalDate today = endPushedAt.toLocalDate();
        String ip = "192.168.1.100";

        UserDetails userDetails = Mockito.mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(String.valueOf(empId));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        // IP 추출용 헤더 모킹
        when(request.getHeader(anyString())).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(ip);

        WorkType workType = new WorkType(1, WorkTypeName.WORK);

        Work existingWork = Work.builder()
                .empId(empId)
                .typeId(workType.getTypeId())
                .startAt(today.atTime(9, 0))
                .endAt(today.atTime(17, 10))
                .startPushedAt(today.atTime(9, 5))
                .breakTime(30)
                .build();

        // repository 및 Validator 스텁 설정
        when(workTypeRepository.findByTypeName(WorkTypeName.WORK)).thenReturn(Optional.of(workType));
        when(workRepository.findByEmpIdAndDateAndTypeName(empId, today, WorkTypeName.WORK)).thenReturn(Optional.of(existingWork));
        when(workUpdateValidator.hasAMHalfDayOff(empId, today)).thenReturn(false);
        when(workUpdateValidator.hasPMHalfDayOff(empId, today)).thenReturn(false);

        LocalDateTime computedEndAt = today.atTime(17, 10);
        when(workTimeService.computeEndAt(endPushedAt, false)).thenReturn(computedEndAt);
        when(workTimeService.getBreakTime(existingWork.getStartAt(), computedEndAt)).thenReturn(30);

        // when
        WorkEndResponse response = workCommandService.updateWork(userDetails, request, endPushedAt);

        // then
        verify(ipValidator).validateIp(eq(ip), anyList());
        verify(workUpdateValidator).validateWorkUpdate(eq(today), eq(endPushedAt), eq(existingWork.getStartAt()), eq(computedEndAt));
        verify(workRepository).findByEmpIdAndDateAndTypeName(empId, today, WorkTypeName.WORK);

        assertNotNull(response);
        assertEquals("퇴근 등록 성공", response.getMessage());

        // 기존 work 엔티티에 endAt, endPushedAt, breakTime 등이 update 됐는지 확인
        assertEquals(computedEndAt, existingWork.getEndAt());
        assertEquals(endPushedAt, existingWork.getEndPushedAt());
        assertEquals(30, existingWork.getBreakTime());
    }
}
