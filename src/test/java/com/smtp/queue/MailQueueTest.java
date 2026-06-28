package com.smtp.queue;
import com.smtp.ratelimit.RateLimiter;
import com.smtp.server.SmtpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailQueueTest {
    @Mock KafkaTemplate<String, String> kafka;
    @Mock RateLimiter limiter;
    @Spy ObjectMapper mapper = new ObjectMapper();
    @InjectMocks MailQueue queue;

    @Test
    void enqueue_withinLimit_sendsToKafka() {
        when(limiter.isAllowed(any())).thenReturn(true);
        var session = new SmtpSession();
        session.setSender("alice@sender.com");
        session.addRecipient("bob@recv.com");
        session.appendBody("Subject: Hi\r\n\r\nHello");
        session.setBodyComplete(true);
        queue.enqueue(session);
        verify(kafka).send(eq("mail-queue"), eq("sender.com"), any());
    }

    @Test
    void enqueue_overLimit_doesNotSendToKafka() {
        when(limiter.isAllowed(any())).thenReturn(false);
        var session = new SmtpSession();
        session.setSender("spammer@spam.com");
        queue.enqueue(session);
        verifyNoInteractions(kafka);
    }
}
