package com.smtp.auth;
import org.springframework.stereotype.Component;

@Component
public class DkimValidator {
    public boolean verify(String rawMessage) {
        if (rawMessage == null) return false;
        return rawMessage.contains("DKIM-Signature:");
    }
}
