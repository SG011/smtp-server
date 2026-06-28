package com.smtp.server;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SmtpSessionTest {
    @Test
    void session_isNotComplete_untilBodySet() {
        var session = new SmtpSession();
        session.setSender("alice@example.com");
        session.addRecipient("bob@example.com");
        assertThat(session.isComplete()).isFalse();
    }

    @Test
    void session_isComplete_whenSenderRecipientsAndBodySet() {
        var session = new SmtpSession();
        session.setSender("alice@example.com");
        session.addRecipient("bob@example.com");
        session.appendBody("Subject: Hello\r\n\r\nHello World");
        session.setBodyComplete(true);
        assertThat(session.isComplete()).isTrue();
    }

    @Test
    void reset_clearsAllFields() {
        var session = new SmtpSession();
        session.setSender("alice@example.com");
        session.reset();
        assertThat(session.getSender()).isNull();
        assertThat(session.getRecipients()).isEmpty();
    }
}
