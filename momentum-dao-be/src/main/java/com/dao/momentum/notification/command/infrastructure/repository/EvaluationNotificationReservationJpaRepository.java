package com.dao.momentum.notification.command.infrastructure.repository;

import com.dao.momentum.notification.command.domain.aggregate.EvaluationNotificationReservation;
import com.dao.momentum.notification.command.domain.repository.EvaluationNotificationReservationRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationNotificationReservationJpaRepository extends EvaluationNotificationReservationRepository, JpaRepository<EvaluationNotificationReservation, Long> {
}
