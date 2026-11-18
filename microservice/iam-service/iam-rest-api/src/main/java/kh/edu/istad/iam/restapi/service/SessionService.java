package kh.edu.istad.iam.restapi.service;

import kh.edu.istad.iam.domain.core.entity.User;
import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.port.out.UserRepository;
import kh.edu.istad.iam.restapi.dto.AdminSessionResponse;
import kh.edu.istad.iam.restapi.dto.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    private final UserRepository userRepository;

    /**
     * Get all active sessions for a user from Redis.
     * Searches by both username and user UUID to guarantee complete resolution.
     */
    public List<SessionResponse> getActiveSessions(String usernameOrUuid, String currentSessionId) {
        Map<String, Session> allSessions = new HashMap<>();

        if (usernameOrUuid != null && !usernameOrUuid.isBlank()) {
            Map<String, ? extends Session> byPrincipal = sessionRepository.findByPrincipalName(usernameOrUuid);
            if (byPrincipal != null) {
                allSessions.putAll(byPrincipal);
            }

            // Cross-resolve between UUID and username in case principal was indexed under the other key
            try {
                UUID uuid = UUID.fromString(usernameOrUuid);
                userRepository.findByUuid(uuid).ifPresent(u -> {
                    if (u.getUsername() != null) {
                        Map<String, ? extends Session> byName = sessionRepository.findByPrincipalName(u.getUsername());
                        if (byName != null) {
                            allSessions.putAll(byName);
                        }
                    }
                });
            } catch (IllegalArgumentException ignored) {
                userRepository.findByUsername(usernameOrUuid).ifPresent(u -> {
                    if (u.getUuid() != null) {
                        Map<String, ? extends Session> byUuid = sessionRepository.findByPrincipalName(u.getUuid().toString());
                        if (byUuid != null) {
                            allSessions.putAll(byUuid);
                        }
                    }
                });
            }
        }

        List<SessionResponse> result = new ArrayList<>();
        for (Session session : allSessions.values()) {
            if (!session.isExpired()) {
                String ip = session.getAttribute("IP_ADDRESS");
                String userAgent = session.getAttribute("USER_AGENT");

                result.add(new SessionResponse(
                        session.getId(),
                        ip != null ? ip : "127.0.0.1 (Localhost)",
                        userAgent != null ? userAgent : "Web Browser",
                        session.getCreationTime(),
                        session.getLastAccessedTime(),
                        session.getId().equals(currentSessionId)
                ));
            }
        }

        // Sort: current session first, then most recently accessed
        result.sort((a, b) -> {
            if (a.isCurrent()) return -1;
            if (b.isCurrent()) return 1;
            if (b.lastAccessedAt() != null && a.lastAccessedAt() != null) {
                return b.lastAccessedAt().compareTo(a.lastAccessedAt());
            }
            return 0;
        });

        return result;
    }

    /**
     * Get all active sessions across all users in the system (for Admin Audit)
     */
    public List<AdminSessionResponse> getAllEnterpriseSessions(String currentSessionId) {
        List<AdminSessionResponse> result = new ArrayList<>();
        PageResult<User> page = userRepository.findAll(0, 100, "username,asc");
        List<User> users = page.content();

        if (users != null) {
            for (User u : users) {
                if (u.getUsername() != null && !u.isDeleted()) {
                    List<SessionResponse> userSessions = getActiveSessions(u.getUsername(), currentSessionId);
                    for (SessionResponse s : userSessions) {
                        result.add(new AdminSessionResponse(
                                s.sessionId(),
                                u.getUsername(),
                                s.ipAddress(),
                                s.device(),
                                s.createdAt(),
                                s.lastAccessedAt(),
                                s.isCurrent()
                        ));
                    }
                }
            }
        }

        return result;
    }

    /**
     * Terminate an individual session by ID
     */
    public void terminateSession(String sessionId) {
        sessionRepository.deleteById(sessionId);
    }

    /**
     * Terminate all other sessions except the current device
     */
    public void terminateAllOtherSessions(String usernameOrUuid, String currentSessionId) {
        List<SessionResponse> sessions = getActiveSessions(usernameOrUuid, currentSessionId);
        for (SessionResponse s : sessions) {
            if (!s.sessionId().equals(currentSessionId)) {
                sessionRepository.deleteById(s.sessionId());
            }
        }
    }
}
