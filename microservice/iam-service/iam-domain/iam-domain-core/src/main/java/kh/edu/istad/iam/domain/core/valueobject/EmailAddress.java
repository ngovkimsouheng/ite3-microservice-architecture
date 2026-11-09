package kh.edu.istad.iam.domain.core.valueobject;

import java.util.Locale;

public record EmailAddress(String value) {

    public EmailAddress {
        if (value == null) {
            throw new IllegalArgumentException("A valid email address is required.");
        }

        value = value.trim().toLowerCase(Locale.ROOT);
        int atIndex = value.indexOf('@');
        if (value.isBlank() || atIndex <= 0 || atIndex == value.length() - 1) {
            throw new IllegalArgumentException("A valid email address is required.");
        }
    }
}
