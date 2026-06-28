package com.smtp.delivery;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smtp.tracking.DeliveryTracker;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.UUID;

@Component
public class DeliveryWorker {
    private final SmtpClient client;
    private final DeliveryTracker tracker;
    private final ObjectMapper mapper;

    public DeliveryWorker(SmtpClient client, DeliveryTracker tracker, ObjectMapper mapper) {
        this.client = client;
        this.tracker = tracker;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "mail-queue", groupId = "smtp-delivery")
    public void onMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        try {
            var node = mapper.readTree(message);
            String sender = node.get("sender").asText();
            List<String> recipients = mapper.convertValue(
                node.get("recipients"), mapper.getTypeFactory().constructCollectionType(List.class, String.class)
            );
            String body = node.get("body").asText();
            boolean success = client.deliver(sender, recipients, body);
            tracker.record(messageId, success ? "DELIVERED" : "SOFT_BOUNCE");
        } catch (Exception e) {
            tracker.record(messageId, "FAILED");
        }
    }
}
