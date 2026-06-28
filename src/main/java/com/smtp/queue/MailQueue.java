package com.smtp.queue;

import com.smtp.server.SmtpSession;

public interface MailQueue {
    void enqueue(SmtpSession session);
}
