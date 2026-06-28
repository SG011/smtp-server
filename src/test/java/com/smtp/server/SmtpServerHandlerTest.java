package com.smtp.server;

import com.smtp.queue.MailQueue;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SmtpServerHandlerTest {

    @Test
    void ehlo_returns250() {
        var queue = mock(MailQueue.class);
        var channel = new EmbeddedChannel(new SmtpServerHandler(queue));
        channel.readOutbound(); // drain 220 greeting
        channel.writeInbound("EHLO client.example.com\r\n");
        String response = channel.readOutbound();
        assertThat(response).startsWith("250");
    }

    @Test
    void fullSmtpConversation_enqueuesToMailQueue() {
        var queue = mock(MailQueue.class);
        var channel = new EmbeddedChannel(new SmtpServerHandler(queue));
        channel.readOutbound(); // drain 220 greeting
        channel.writeInbound("EHLO sender.com\r\n");
        channel.readOutbound(); // 250
        channel.writeInbound("MAIL FROM:<alice@sender.com>\r\n");
        channel.readOutbound(); // 250
        channel.writeInbound("RCPT TO:<bob@receiver.com>\r\n");
        channel.readOutbound(); // 250
        channel.writeInbound("DATA\r\n");
        channel.readOutbound(); // 354
        channel.writeInbound("Subject: Test\r\n\r\nHello\r\n");
        channel.writeInbound(".\r\n");
        channel.readOutbound(); // 250
        verify(queue).enqueue(any());
    }
}
