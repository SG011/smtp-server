package com.smtp.delivery;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SmtpClient {
    public boolean deliver(String sender, List<String> recipients, String body) {
        try {
            // Portfolio simulation: 95% success rate
            return Math.random() > 0.05;
        } catch (Exception e) { return false; }
    }
}
