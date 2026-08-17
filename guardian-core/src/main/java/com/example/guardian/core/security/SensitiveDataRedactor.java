package com.example.guardian.core.security;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Central redaction utility used before source evidence leaves the scanner core.
 *
 * <p>The scanner can legitimately inspect configuration files containing credentials. Those
 * credentials must never be copied into JSON, HTML or text reports.</p>
 */
public final class SensitiveDataRedactor {

    private static final String SECRET_KEY = "(?:password|passwd|pwd|secret|token|api[-_]?key|client[-_]?secret|access[-_]?key|private[-_]?key)";

    private static final Pattern KEY_VALUE = Pattern.compile(
            "(?im)([\"']?" + SECRET_KEY + "[\"']?\\s*[:=]\\s*)([^\\r\\n,;}#]+)"
    );
    private static final Pattern BEARER = Pattern.compile("(?i)(bearer\\s+)[A-Za-z0-9._~+\\-/=]{6,}");
    private static final Pattern BASIC = Pattern.compile("(?i)(basic\\s+)[A-Za-z0-9+/=]{8,}");
    private static final Pattern AWS_ACCESS_KEY = Pattern.compile("\\b(?:AKIA|ASIA)[A-Z0-9]{16}\\b");
    private static final Pattern PRIVATE_KEY = Pattern.compile("(?s)-----BEGIN(?: [A-Z0-9]+)? PRIVATE KEY-----.*?-----END(?: [A-Z0-9]+)? PRIVATE KEY-----");

    private SensitiveDataRedactor() {
    }

    /**
     * Redacts common credentials while preserving the surrounding source line for diagnostics.
     *
     * @param value evidence or source snippet
     * @return redacted value, or {@code null} when the input is null
     */
    public static String redact(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        String redacted = PRIVATE_KEY.matcher(value).replaceAll("-----BEGIN PRIVATE KEY----- ****** -----END PRIVATE KEY-----");
        redacted = replaceKeyValues(redacted);
        redacted = BEARER.matcher(redacted).replaceAll("$1******");
        redacted = BASIC.matcher(redacted).replaceAll("$1******");
        redacted = AWS_ACCESS_KEY.matcher(redacted).replaceAll("******");
        return redacted;
    }

    private static String replaceKeyValues(String value) {
        Matcher matcher = KEY_VALUE.matcher(value);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String replacement = matcher.group(1) + "******";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
