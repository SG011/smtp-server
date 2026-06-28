package com.smtp.queue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smtp.ratelimit.RateLimiter;
import com.smtp.server.SmtpSession;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class MailQueue {
    private static final String TOPIC = "mail-queue";
    private final KafkaTemplate<String, String> kafka;
    private final RateLimiter limiter;
    private final ObjectMapper mapper;

    public MailQueue(KafkaTemplate<String, String> kafka, RateLimiter limiter, ObjectMapper mapper) {
        this.kafka = kafka;
        this.limiter = limiter;
        this.mapper = mapper;
    }

    public void enqueue(SmtpSession session) {
        String senderDomain = extractDomain(session.getSender());
        if (!limiter.isAllowed(senderDomain)) return;
        try {
            String payload = mapper.writeValueAsString(Map.of(
                "sender", session.getSender(),
                "recipients", session.getRecipients(),
                "body", session.getBody(),
                "senderDomain", senderDomain
            ));
            kafka.send(TOPIC, senderDomain, payload);
        } catch (Exception e) { throw new RuntimeException("Failed to enqueue mail", e); }
    }

    private String extractDomain(String email) {
        if (email == null || !email.contains("@")) return "unknown";
        return email.substring(email.indexOf('@') + 1);
    }
}
