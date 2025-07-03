package com.dao.momentum.notification.command.domain.repository;

import com.dao.momentum.notification.command.domain.aggregate.EvaluationNotificationReservation;

public interface EvaluationNotificationReservationRepository {
    EvaluationNotificationReservation save(EvaluationNotificationReservation reservation);
}