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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;
    private final UserRepository userRepository;

    /**
     * Get all active sessions for a user from Redis
     */
    public List<SessionResponse> getActiveSessions(String username, String currentSessionId) {
        Map<String, ? extends Session> sessions = sessionRepository.findByPrincipalName(username);
        List<SessionResponse> result = new ArrayList<>();

        for (Session session : sessions.values()) {
            if (!session.isExpired()) {
                String ip = session.getAttribute("IP_ADDRESS");
                String userAgent = session.getAttribute("USER_AGENT");

                result.add(new SessionResponse(
                        session.getId(),
                        ip != null ? ip : "127.0.0.1 (Localhost)",
                        userAgent != null ? userAgent : "Desktop Browser",
                        session.getCreationTime(),
                        session.getLastAccessedTime(),
                        session.getId().equals(currentSessionId)
                ));
            }
        }
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
                    Map<String, ? extends Session> userSessions = sessionRepository.findByPrincipalName(u.getUsername());
                    if (userSessions != null) {
                        for (Session session : userSessions.values()) {
                            if (!session.isExpired()) {
                                String ip = session.getAttribute("IP_ADDRESS");
                                String userAgent = session.getAttribute("USER_AGENT");

                                result.add(new AdminSessionResponse(
                                        session.getId(),
                                        u.getUsername(),
                                        ip != null ? ip : "127.0.0.1 (Localhost)",
                                        userAgent != null ? userAgent : "Web Browser",
                                        session.getCreationTime(),
                                        session.getLastAccessedTime(),
                                        session.getId().equals(currentSessionId)
                                ));
                            }
                        }
                    }
                }
            }
        }

        // If no active sessions in Redis, return empty list
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
    public void terminateAllOtherSessions(String username, String currentSessionId) {
        Map<String, ? extends Session> sessions = sessionRepository.findByPrincipalName(username);
        for (String id : sessions.keySet()) {
            if (!id.equals(currentSessionId)) {
                sessionRepository.deleteById(id);
            }
        }
    }
}
