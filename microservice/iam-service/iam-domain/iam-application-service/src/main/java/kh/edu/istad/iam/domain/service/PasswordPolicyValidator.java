package kh.edu.istad.iam.domain.service;

import kh.edu.istad.iam.domain.core.entity.SystemConfiguration;
import kh.edu.istad.iam.domain.port.out.SystemConfigurationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordPolicyValidator {

    private final SystemConfigurationRepository systemConfigurationRepository;

    public void validate(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be blank.");
        }

        SystemConfiguration config = systemConfigurationRepository.getConfiguration();
        if (config == null) {
            config = SystemConfiguration.defaultConfiguration();
        }

        List<String> violations = new ArrayList<>();

        int minLen = config.getMinPasswordLength() != null ? config.getMinPasswordLength() : 8;
        if (password.length() < minLen) {
            violations.add("must be at least " + minLen + " characters long");
        }

        if (Boolean.TRUE.equals(config.getRequireUppercase()) && !password.chars().anyMatch(Character::isUpperCase)) {
            violations.add("must contain at least one uppercase letter (A-Z)");
        }

        if (Boolean.TRUE.equals(config.getRequireLowercase()) && !password.chars().anyMatch(Character::isLowerCase)) {
            violations.add("must contain at least one lowercase letter (a-z)");
        }

        if (Boolean.TRUE.equals(config.getRequireDigits()) && !password.chars().anyMatch(Character::isDigit)) {
            violations.add("must contain at least one numerical digit (0-9)");
        }

        if (Boolean.TRUE.equals(config.getRequireSpecialChar())) {
            boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
            if (!hasSpecial) {
                violations.add("must contain at least one special character (@$!%*?&)");
            }
        }

        if (!violations.isEmpty()) {
            String message = "Password does not meet enterprise security requirements: " + String.join(", ", violations) + ".";
            log.warn("Password policy validation failed: {}", message);
            throw new IllegalArgumentException(message);
        }
    }
}
