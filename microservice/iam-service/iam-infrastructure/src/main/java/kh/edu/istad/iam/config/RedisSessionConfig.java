package kh.edu.istad.iam.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisIndexedHttpSession;

@Configuration
// Configures Redis sessions with a 7-day TTL (604800 seconds)
// Enables querying sessions by username (Principal name)
@EnableRedisIndexedHttpSession(maxInactiveIntervalInSeconds = 604800)
public class RedisSessionConfig {
    // Spring Session automatically sets up the Redis repository and serializers!
}