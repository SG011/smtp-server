package com.smtp.bounce;
import org.springframework.stereotype.Component;

@Component
public class BounceHandler {
    public boolean isHardBounce(String errorCode) {
        return errorCode != null && errorCode.startsWith("5");
    }
}
