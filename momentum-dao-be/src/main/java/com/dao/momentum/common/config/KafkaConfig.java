package com.dao.momentum.common.config;

import com.dao.momentum.common.kafka.dto.NotificationMessage;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StringSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    /* ProducerFactory 설정
     * - Kafka에 메시지를 보내는(produce) 역할을 수행
     * - 서버 주소, 키/값 직렬화 방식 등을 지정
     */
    @Bean
    public ProducerFactory<String, NotificationMessage> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        // 카프카 클러스터의 호스트:포트 (Docker Compose나 외부 서버 등)
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");

        // 메시지 키를 String 으로 직렬화
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // 메시지 값을 JSON 으로 직렬화 (NotificationMessage 객체 → JSON 문자열)
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    /* KafkaTemplate 빈 등록
     * - 메시지를 보내기 위한 편리한 API 제공
     * - producerFactory를 통해 생성된 프로듀서를 내부에서 사용
     */
    @Bean
    public KafkaTemplate<String, NotificationMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
