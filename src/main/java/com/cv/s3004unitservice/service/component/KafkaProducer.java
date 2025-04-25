package com.cv.s3004unitservice.service.component;

import com.cv.s0402notifyservicepojo.dto.NotifyDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.io.Serializable;

@Slf4j
@Component
public class KafkaProducer implements Serializable {

    @Serial
    private static final long serialVersionUID = -5120381418389428256L;

    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.notify-service.topic}")
    private String notifyTopic;

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object object) {
        kafkaTemplate.send(topic, object)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("❌ Failed to send notification to topic {}: {}", topic, ex.getMessage(), ex);
                    } else {
                        log.info("✅ Notification sent to topic {}: partition={}, offset={}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }

    public void notify(NotifyDto dto) {
        log.info("KafkaProducer.notify topic: {}, {}", notifyTopic, dto.toString());
        send(notifyTopic, dto);
    }

}
