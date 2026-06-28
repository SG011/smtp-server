package com.smtp.tracking;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class DeliveryTracker {

    private final DeliveryEventRepository repo;

    public DeliveryTracker(DeliveryEventRepository repo) {
        this.repo = repo;
    }

    public void record(String messageId, String status) {
        var event = new DeliveryEvent();
        event.setMessageId(messageId);
        event.setRecordedAt(Instant.now());
        event.setStatus(status);
        repo.save(event);
    }
}
