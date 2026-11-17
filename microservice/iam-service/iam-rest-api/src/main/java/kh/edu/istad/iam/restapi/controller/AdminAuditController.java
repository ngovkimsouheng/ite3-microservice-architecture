package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import kh.edu.istad.iam.domain.core.entity.UserSecurityEvent;
import kh.edu.istad.iam.domain.port.out.UserSecurityEventRepository;
import kh.edu.istad.iam.restapi.dto.AdminSessionResponse;
import kh.edu.istad.iam.restapi.dto.LoginHistoryResponse;
import kh.edu.istad.iam.restapi.dto.SecurityActivityResponse;
import kh.edu.istad.iam.restapi.service.SessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
@RequiredArgsConstructor
@Slf4j
public class AdminAuditController {

    private final UserSecurityEventRepository securityEventRepository;
    private final SessionService sessionService;

    @GetMapping("/login-history")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<LoginHistoryResponse>> getEnterpriseLoginHistory(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") String status,
            @RequestParam(name = "limit", required = false, defaultValue = "100") int limit
    ) {
        List<UserSecurityEvent> events = securityEventRepository.findAllLoginHistory(status, limit);
        List<LoginHistoryResponse> response = events.stream().map(e -> new LoginHistoryResponse(
                String.valueOf(e.getId()),
                e.getStatus(),
                e.getCreatedAt(),
                formatIp(e.getIpAddress()),
                e.getDevice() != null ? e.getDevice() : "Web Browser",
                e.getLocation() != null ? e.getLocation() : "Phnom Penh, Cambodia",
                e.getAuthMethod() != null ? e.getAuthMethod() : "Password"
        )).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/security-activity")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<SecurityActivityResponse>> getEnterpriseSecurityActivity(
            @RequestParam(name = "limit", required = false, defaultValue = "100") int limit
    ) {
        List<UserSecurityEvent> events = securityEventRepository.findAllActivities(limit);
        List<SecurityActivityResponse> response = events.stream().map(e -> {
            String icon = resolveIcon(e.getEventType());
            String color = resolveColor(e.getEventType());
            String desc = (e.getUsername() != null && !e.getUsername().isBlank() ? "@" + e.getUsername() + " — " : "")
                    + (e.getDetails() != null ? e.getDetails() : (e.getDescription() != null ? e.getDescription() : e.getEventType()));
            return new SecurityActivityResponse(
                    String.valueOf(e.getId()),
                    e.getDescription() != null ? e.getDescription() : e.getEventType(),
                    desc,
                    e.getCreatedAt(),
                    icon,
                    color,
                    formatIp(e.getIpAddress()),
                    e.getEventType(),
                    e.getUsername()
            );
        }).toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<AdminSessionResponse>> getEnterpriseSessions(HttpServletRequest request) {
        String currentSessionId = request.getSession().getId();
        List<AdminSessionResponse> sessions = sessionService.getAllEnterpriseSessions(currentSessionId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/deactivation-requests")
    @PreAuthorize("hasAuthority('role:read') or hasRole('ADMIN')")
    public ResponseEntity<List<SecurityActivityResponse>> getDeactivationRequests(
            @RequestParam(name = "limit", required = false, defaultValue = "50") int limit
    ) {
        List<UserSecurityEvent> events = securityEventRepository.findDeactivationRequests(limit);
        List<SecurityActivityResponse> response = events.stream().map(e -> new SecurityActivityResponse(
                String.valueOf(e.getId()),
                e.getDescription() != null ? e.getDescription() : "Account Deactivation Requested",
                (e.getUsername() != null ? "@" + e.getUsername() + " — " : "") + (e.getDetails() != null ? e.getDetails() : "Account deactivation requested"),
                e.getCreatedAt(),
                "ShieldAlert",
                "bg-red-50 text-[#DF1F28]"
        )).toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/sessions/{sessionId}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> terminateSession(@PathVariable("sessionId") String sessionId) {
        sessionService.terminateSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    private String formatIp(String ip) {
        if (ip == null || ip.isBlank() || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.equals("127.0.0.1")) {
            return "127.0.0.1 (Localhost)";
        }
        return ip.trim();
    }

    private String resolveIcon(String eventType) {
        if (eventType == null) return "Activity";
        return switch (eventType.toUpperCase()) {
            case "PASSWORD_CHANGED", "PASSWORD_RESET" -> "KeyRound";
            case "PROFILE_UPDATED", "USERNAME_CHANGED", "USER_CREATED", "USER_ENABLED", "USER_UNLOCKED" -> "UserCheck";
            case "USER_LOCKED", "USER_DISABLED", "USER_DELETED" -> "UserX";
            case "ROLE_ASSIGNED", "ROLE_REVOKED", "ROLE_CREATED", "ROLE_UPDATED", "ROLE_DELETED" -> "Shield";
            case "PERMISSION_GRANTED", "PERMISSION_REVOKED", "ROLE_PERMISSION_ASSIGNED", "ROLE_PERMISSION_REVOKED" -> "Key";
            case "CLIENT_REGISTERED", "CLIENT_UPDATED", "CLIENT_DELETED" -> "Layers";
            case "SYSTEM_CONFIG_UPDATED", "SYSTEM_CONFIG_RESET" -> "Settings";
            case "IDENTITY_LINKED", "IDENTITY_UNLINKED" -> "Lock";
            case "SESSION_TERMINATED", "DEACTIVATION_REQUESTED", "ACCOUNT_DEACTIVATED" -> "ShieldAlert";
            default -> "Activity";
        };
    }

    private String resolveColor(String eventType) {
        if (eventType == null) return "bg-[#253C95]/10 text-[#253C95]";
        return switch (eventType.toUpperCase()) {
            case "PASSWORD_CHANGED", "PASSWORD_RESET" -> "bg-[#F9A825]/10 text-[#F9A825]";
            case "PROFILE_UPDATED", "USERNAME_CHANGED", "USER_CREATED", "USER_ENABLED", "USER_UNLOCKED" -> "bg-emerald-50 text-emerald-600";
            case "ROLE_ASSIGNED", "ROLE_REVOKED", "ROLE_CREATED", "ROLE_UPDATED" -> "bg-blue-50 text-[#253C95]";
            case "PERMISSION_GRANTED", "PERMISSION_REVOKED", "ROLE_PERMISSION_ASSIGNED", "ROLE_PERMISSION_REVOKED" -> "bg-purple-50 text-purple-600";
            case "CLIENT_REGISTERED", "CLIENT_UPDATED" -> "bg-indigo-50 text-indigo-600";
            case "IDENTITY_LINKED", "IDENTITY_UNLINKED" -> "bg-purple-50 text-purple-600";
            case "USER_LOCKED", "USER_DISABLED", "USER_DELETED", "SESSION_TERMINATED", "DEACTIVATION_REQUESTED", "ACCOUNT_DEACTIVATED", "CLIENT_DELETED" -> "bg-[#DF1F28]/10 text-[#DF1F28]";
            default -> "bg-[#253C95]/10 text-[#253C95]";
        };
    }
}
