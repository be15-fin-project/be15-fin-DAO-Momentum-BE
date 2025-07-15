package com.dao.momentum.approve.command.application.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public interface FormDetailStrategy {

    /* 결재 내역을 저장하는 메소드 */
    void saveDetail(JsonNode form, Long approveId);

    /* 알림 메시지를 생성하는 메소드 (요청 / 승인 / 반려 등 상황별) */
    String createNotificationContent(Long approveId, String senderName, NotificationType type);

    enum NotificationType {
        REQUEST,    // 결재 요청
        APPROVED,   // 최종 승인
        REJECTED    // 반려
    }
}