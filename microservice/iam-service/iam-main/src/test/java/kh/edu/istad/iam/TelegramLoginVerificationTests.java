package kh.edu.istad.iam;

import kh.edu.istad.iam.domain.port.out.TelegramLoginReplayGuard;
import kh.edu.istad.iam.domain.usecase.identity.VerifyTelegramLoginUseCase;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class TelegramLoginVerificationTests {

    private static final String BOT_TOKEN = "test-bot-token";

    @Test
    void acceptsValidRecentSignatureAndConsumesItOnce() throws Exception {
        TelegramLoginReplayGuard replayGuard = mock(TelegramLoginReplayGuard.class);
        when(replayGuard.consume(anyString(), any())).thenReturn(true);
        VerifyTelegramLoginUseCase useCase = new VerifyTelegramLoginUseCase(replayGuard);
        Map<String, String> parameters = signedParameters();

        var command = useCase.execute(parameters, BOT_TOKEN);

        assertEquals("TELEGRAM", command.provider());
        assertEquals("123456789", command.providerSubject());
        assertEquals("Ada Lovelace", command.displayName());
        verify(replayGuard).consume(eq(parameters.get("hash")), any());
    }

    @Test
    void rejectsTamperedSignatureBeforeConsumingReplayToken() throws Exception {
        TelegramLoginReplayGuard replayGuard = mock(TelegramLoginReplayGuard.class);
        VerifyTelegramLoginUseCase useCase = new VerifyTelegramLoginUseCase(replayGuard);
        Map<String, String> parameters = signedParameters();
        parameters.put("username", "attacker");

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(parameters, BOT_TOKEN));
        verifyNoInteractions(replayGuard);
    }

    @Test
    void rejectsExpiredAuthenticationData() throws Exception {
        TelegramLoginReplayGuard replayGuard = mock(TelegramLoginReplayGuard.class);
        VerifyTelegramLoginUseCase useCase = new VerifyTelegramLoginUseCase(replayGuard);
        Map<String, String> parameters = signedParameters();
        parameters.put("auth_date", Long.toString(Instant.now().getEpochSecond() - 301));
        parameters.put("hash", sign(parameters));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(parameters, BOT_TOKEN));
        verifyNoInteractions(replayGuard);
    }

    @Test
    void rejectsReplayedAuthenticationData() throws Exception {
        TelegramLoginReplayGuard replayGuard = mock(TelegramLoginReplayGuard.class);
        when(replayGuard.consume(anyString(), any())).thenReturn(false);
        VerifyTelegramLoginUseCase useCase = new VerifyTelegramLoginUseCase(replayGuard);
        Map<String, String> parameters = signedParameters();

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(parameters, BOT_TOKEN));
        verify(replayGuard).consume(eq(parameters.get("hash")), any());
    }

    private Map<String, String> signedParameters() throws Exception {
        Map<String, String> parameters = new LinkedHashMap<>();
        parameters.put("id", "123456789");
        parameters.put("first_name", "Ada");
        parameters.put("last_name", "Lovelace");
        parameters.put("username", "ada");
        parameters.put("auth_date", Long.toString(Instant.now().getEpochSecond()));
        parameters.put("hash", sign(parameters));
        return parameters;
    }

    private String sign(Map<String, String> parameters) throws Exception {
        String checkString = parameters.entrySet().stream()
                .filter(entry -> !"hash".equals(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(java.util.stream.Collectors.joining("\n"));
        byte[] secret = MessageDigest.getInstance("SHA-256")
                .digest(BOT_TOKEN.getBytes(StandardCharsets.UTF_8));
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(checkString.getBytes(StandardCharsets.UTF_8)));
    }
}
