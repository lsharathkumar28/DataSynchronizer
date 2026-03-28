package com.outreach.datasynchronizer.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserChangeEventPublisher {

    public static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserChangeEvent> kafkaTemplate;

    public void publish(UserChangeEvent event) {
        log.info("Publishing {} event for userId={}", event.getChangeType(), event.getUserId());
        kafkaTemplate.send(TOPIC, event.getUserId().toString(), event);
    }
}

