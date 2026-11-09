package kh.edu.istad.iam.domain.port.out;

import java.time.OffsetDateTime;

public interface TelegramLoginReplayGuard {
    boolean consume(String authenticationHash, OffsetDateTime expiresAt);
}
