package sap.ass2.users.application;

import org.springframework.kafka.annotation.KafkaListener;

public interface UserEventsConsumer {

    @KafkaListener(topics = "user-events", groupId = "users-manager")
    public void consumeEvents(String message);
}