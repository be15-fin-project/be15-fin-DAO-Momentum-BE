package com.dao.momentum.notification.command.application.service;

import com.dao.momentum.notification.command.domain.aggregate.EvaluationNotificationReservation;
import com.dao.momentum.notification.command.domain.aggregate.IsSent;
import com.dao.momentum.notification.command.domain.repository.EvaluationNotificationReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final EvaluationNotificationReservationRepository repository;

    public void saveReservation(LocalDate scheduledDate, LocalDate startDate, LocalDate endDate, String type) {
        EvaluationNotificationReservation reservation = EvaluationNotificationReservation.builder()
                .type(type)
                .scheduledDate(scheduledDate.atTime(10, 0))  // 발송 예정 시간: 오전 10시
                .startDate(startDate.atStartOfDay())
                .endDate(endDate.atTime(23, 59, 59))         // 평가 종료일: 23:59:59
                .isSent(IsSent.N)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        repository.save(reservation);
    }
}
