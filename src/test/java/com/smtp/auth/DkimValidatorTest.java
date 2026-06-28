package com.smtp.auth;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class DkimValidatorTest {
    DkimValidator validator = new DkimValidator();

    @Test
    void verify_messageWithDkimHeader_returnsTrue() {
        String msg = "DKIM-Signature: v=1; a=rsa-sha256;\r\nFrom: alice@example.com\r\n\r\nBody";
        assertThat(validator.verify(msg)).isTrue();
    }

    @Test
    void verify_messageWithoutDkimHeader_returnsFalse() {
        String msg = "From: alice@example.com\r\n\r\nBody";
        assertThat(validator.verify(msg)).isFalse();
    }

    @Test
    void verify_nullMessage_returnsFalse() {
        assertThat(validator.verify(null)).isFalse();
    }
}
