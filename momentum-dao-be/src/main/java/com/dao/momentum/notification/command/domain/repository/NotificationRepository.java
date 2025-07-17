package com.dao.momentum.notification.command.domain.repository;

import com.dao.momentum.notification.command.domain.aggregate.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Modifying
    @Query("DELETE FROM Notification n WHERE n.approveId = :approveId")
    void deleteNotificationByApproveId(Long approveId);

}
