package com.smtp.auth;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class SpfValidatorTest {
    SpfValidator validator = new SpfValidator();

    @Test
    void validate_unknownDomain_returnsNone() {
        SpfResult result = validator.validate("user@nonexistent-domain-xyz123.com", "1.2.3.4");
        assertThat(result).isEqualTo(SpfResult.NONE);
    }

    @Test
    void validate_nullInputs_returnsNone() {
        assertThat(validator.validate(null, null)).isEqualTo(SpfResult.NONE);
    }
}
