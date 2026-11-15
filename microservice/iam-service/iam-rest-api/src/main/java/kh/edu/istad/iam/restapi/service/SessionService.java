package kh.edu.istad.iam.restapi.service;

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

    // Automatically provided by Spring Session Redis
    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

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
                        ip != null ? ip : "127.0.0.1",
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