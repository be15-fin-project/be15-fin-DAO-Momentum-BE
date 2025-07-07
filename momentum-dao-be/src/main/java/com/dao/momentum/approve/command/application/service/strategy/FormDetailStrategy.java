package com.dao.momentum.approve.command.application.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public interface FormDetailStrategy {

    /* 결재 내역을 저장하는 메소드 */
    void saveDetail(JsonNode form, Long approveId);

    /* 알림 메세지를 생성하는 메소드 */
    String createNotificationContent(Long approveId, String senderName);

}