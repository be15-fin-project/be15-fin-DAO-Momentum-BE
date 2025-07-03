package com.dao.momentum.notification.command.application.service;

import com.dao.momentum.common.kafka.dto.NotificationMessage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notificationClient", url = "${batch.server.url}")
public interface NotificationClient {

    @PostMapping("/evaluation-notifications/send")
    void sendEvaluationStart(@RequestBody NotificationMessage message);
}
