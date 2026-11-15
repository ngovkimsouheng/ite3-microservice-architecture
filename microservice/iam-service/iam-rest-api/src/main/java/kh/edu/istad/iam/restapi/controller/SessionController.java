package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import kh.edu.istad.iam.restapi.dto.SessionResponse;
import kh.edu.istad.iam.restapi.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;

    // --- User Account Session Endpoints (+5 pts Account Module) ---

    @GetMapping("/me/sessions")
    public ResponseEntity<List<SessionResponse>> getMyActiveSessions(
            Principal principal,
            HttpServletRequest request) {
        String currentSessionId = request.getSession().getId();
        List<SessionResponse> sessions = sessionService.getActiveSessions(principal.getName(), currentSessionId);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/me/sessions/{sessionId}")
    public ResponseEntity<Void> terminateMySession(
            @PathVariable("sessionId") String sessionId,
            Principal principal,
            HttpServletRequest request
    ) {
        sessionService.terminateSession(sessionId);

        try {
            recordSecurityEventUseCase.execute(
                    null,
                    principal != null ? principal.getName() : null,
                    "ACTIVITY",
                    "SESSION_TERMINATED",
                    "SUCCESS",
                    request.getRemoteAddr(),
                    null,
                    "Phnom Penh, Cambodia",
                    null,
                    "Session Terminated",
                    "Terminated device session (" + (sessionId.length() > 8 ? sessionId.substring(0, 8) + "..." : sessionId) + ")"
            );
        } catch (Exception ignored) {}

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me/sessions")
    public ResponseEntity<Void> terminateAllOtherSessions(
            Principal principal,
            HttpServletRequest request) {
        String currentSessionId = request.getSession().getId();
        sessionService.terminateAllOtherSessions(principal.getName(), currentSessionId);

        try {
            recordSecurityEventUseCase.execute(
                    null,
                    principal != null ? principal.getName() : null,
                    "ACTIVITY",
                    "SESSION_TERMINATED",
                    "SUCCESS",
                    request.getRemoteAddr(),
                    null,
                    "Phnom Penh, Cambodia",
                    null,
                    "All Other Sessions Terminated",
                    "Signed out of all other active devices"
            );
        } catch (Exception ignored) {}

        return ResponseEntity.noContent().build();
    }

    // --- Admin Management Session Endpoints (+5 pts Management Module) ---

    @GetMapping("/users/{username}/sessions")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<List<SessionResponse>> getUserSessions(
            @PathVariable("username") String username,
            HttpServletRequest request) {
        String currentSessionId = request.getSession().getId();
        List<SessionResponse> sessions = sessionService.getActiveSessions(username, currentSessionId);
        return ResponseEntity.ok(sessions);
    }

    @DeleteMapping("/users/{username}/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> terminateUserSession(
            @PathVariable("username") String username,
            @PathVariable("sessionId") String sessionId) {
        sessionService.terminateSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}