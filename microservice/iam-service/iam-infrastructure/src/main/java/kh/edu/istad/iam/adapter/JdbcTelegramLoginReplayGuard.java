package kh.edu.istad.iam.adapter;

import kh.edu.istad.iam.domain.port.out.TelegramLoginReplayGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
@RequiredArgsConstructor
public class JdbcTelegramLoginReplayGuard implements TelegramLoginReplayGuard {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean consume(String authenticationHash, OffsetDateTime expiresAt) {
        jdbcTemplate.update("DELETE FROM telegram_login_replay WHERE expires_at < CURRENT_TIMESTAMP");
        return jdbcTemplate.update("""
                INSERT INTO telegram_login_replay (authentication_hash, expires_at)
                VALUES (?, ?)
                ON CONFLICT (authentication_hash) DO NOTHING
                """, authenticationHash, expiresAt) == 1;
    }
}
