package com.dao.momentum.approve.command.application.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public interface FormDetailStrategy {

    void saveDetail(JsonNode form, Long approveId);
    String createNotificationContent(Long approveId, String senderName);
}