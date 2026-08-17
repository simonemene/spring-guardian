package com.example.guardian.core.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SensitiveDataRedactorTest {

    @Test
    void redactsPropertyYamlAndAuthorizationSecrets() {
        String input = "spring.datasource.password=SuperPassword123!\n"
                + "client-secret: abcdefghijkl\n"
                + "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.secret.signature";

        String redacted = SensitiveDataRedactor.redact(input);

        assertFalse(redacted.contains("SuperPassword123!"));
        assertFalse(redacted.contains("abcdefghijkl"));
        assertFalse(redacted.contains("eyJhbGciOiJIUzI1NiJ9.secret.signature"));
        assertTrue(redacted.contains("******"));
    }
}
