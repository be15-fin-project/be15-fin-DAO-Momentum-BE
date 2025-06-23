package com.dao.momentum.common.kafka.producer;

import com.dao.momentum.common.kafka.dto.NotificationMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    @Value("${custom.kafka.notification-topic}")
    private String topic;

    public NotificationKafkaProducer(KafkaTemplate<String, NotificationMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * userId를 key로 설정하면 Kafka가 같은 유저 메시지를 동일한 파티션에 보냄
     */
    public void sendNotification(String userId, NotificationMessage message) {
        kafkaTemplate.send(topic, userId, message);
    }
}

