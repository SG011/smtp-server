package com.smtp.delivery;
import com.smtp.tracking.DeliveryTracker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryWorkerTest {
    @Mock DeliveryTracker tracker;
    @Mock SmtpClient client;
    @Spy com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    @InjectMocks DeliveryWorker worker;

    @Test
    void onMessage_successfulDelivery_recordsDelivered() throws Exception {
        when(client.deliver(any(), any(), any())).thenReturn(true);
        String msg = """
            {"sender":"alice@s.com","recipients":["bob@r.com"],"body":"Hello","senderDomain":"s.com"}
        """;
        worker.onMessage(msg);
        verify(tracker).record(any(), eq("DELIVERED"));
    }

    @Test
    void onMessage_failedDelivery_recordsBounce() throws Exception {
        when(client.deliver(any(), any(), any())).thenReturn(false);
        String msg = """
            {"sender":"alice@s.com","recipients":["bad@r.com"],"body":"Hello","senderDomain":"s.com"}
        """;
        worker.onMessage(msg);
        verify(tracker).record(any(), eq("SOFT_BOUNCE"));
    }
}
