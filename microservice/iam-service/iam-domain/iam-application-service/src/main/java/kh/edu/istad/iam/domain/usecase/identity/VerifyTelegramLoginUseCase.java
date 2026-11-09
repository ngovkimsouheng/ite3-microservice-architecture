package kh.edu.istad.iam.domain.usecase.identity;

import kh.edu.istad.iam.domain.dto.FederatedIdentityCommand;
import kh.edu.istad.iam.domain.port.out.TelegramLoginReplayGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class VerifyTelegramLoginUseCase {

    private static final long MAX_AUTH_AGE_SECONDS = 300;
    private static final long FUTURE_CLOCK_SKEW_SECONDS = 30;
    private final TelegramLoginReplayGuard replayGuard;

    @Transactional
    public FederatedIdentityCommand execute(Map<String, String> parameters, String botToken) {
        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException("TELEGRAM_BOT_TOKEN must be configured to enable Telegram login.");
        }
        String providedHash = parameters.get("hash");
        if (providedHash == null || !providedHash.matches("[0-9a-fA-F]{64}")) {
            throw new IllegalArgumentException("Invalid Telegram login signature.");
        }
        String authDateValue = parameters.get("auth_date");
        long authDate;
        try {
            authDate = Long.parseLong(authDateValue);
        } catch (RuntimeException exception) {
            throw new IllegalArgumentException("Invalid Telegram authentication timestamp.", exception);
        }
        long now = Instant.now().getEpochSecond();
        if (authDate < now - MAX_AUTH_AGE_SECONDS || authDate > now + FUTURE_CLOCK_SKEW_SECONDS) {
            throw new IllegalArgumentException("Telegram authentication data is expired or from the future.");
        }

        TreeMap<String, String> signedValues = new TreeMap<>(parameters);
        signedValues.remove("hash");
        String checkString = signedValues.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(java.util.stream.Collectors.joining("\n"));
        byte[] secretKey = sha256(botToken.getBytes(StandardCharsets.UTF_8));
        byte[] expectedHash;
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secretKey, "HmacSHA256"));
            expectedHash = hmac.doFinal(checkString.getBytes(StandardCharsets.UTF_8));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to verify Telegram login data.", exception);
        }

        byte[] actualHash;
        try {
            actualHash = HexFormat.of().parseHex(providedHash);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid Telegram login signature.", exception);
        }
        if (!MessageDigest.isEqual(expectedHash, actualHash)) {
            throw new IllegalArgumentException("Invalid Telegram login signature.");
        }
        if (!replayGuard.consume(providedHash.toLowerCase(), OffsetDateTime.now(ZoneOffset.UTC)
                .plusSeconds(MAX_AUTH_AGE_SECONDS))) {
            throw new IllegalArgumentException("Telegram login data has already been used.");
        }

        String userId = parameters.get("id");
        if (userId == null || !userId.matches("[0-9]{1,20}")) {
            throw new IllegalArgumentException("Invalid Telegram user identifier.");
        }
        String displayName = ((parameters.getOrDefault("first_name", "") + " "
                + parameters.getOrDefault("last_name", "")).trim());
        Map<String, Object> attributes = new java.util.LinkedHashMap<>();
        attributes.put("id", userId);
        if (parameters.containsKey("first_name")) attributes.put("first_name", parameters.get("first_name"));
        if (parameters.containsKey("last_name")) attributes.put("last_name", parameters.get("last_name"));
        if (parameters.containsKey("username")) attributes.put("username", parameters.get("username"));
        if (parameters.containsKey("photo_url")) attributes.put("photo_url", parameters.get("photo_url"));

        return new FederatedIdentityCommand(
                "TELEGRAM",
                userId,
                null,
                false,
                displayName,
                parameters.get("username"),
                parameters.get("photo_url"),
                attributes
        );
    }

    private byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }
}
