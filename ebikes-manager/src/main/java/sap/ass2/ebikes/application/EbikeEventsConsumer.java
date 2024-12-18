package sap.ass2.ebikes.application;

import org.springframework.kafka.annotation.KafkaListener;

public interface EbikeEventsConsumer {

    @KafkaListener(topics = "ebike-events", groupId = "ebikes-manager")
    public void consumeEvents(String message);
}