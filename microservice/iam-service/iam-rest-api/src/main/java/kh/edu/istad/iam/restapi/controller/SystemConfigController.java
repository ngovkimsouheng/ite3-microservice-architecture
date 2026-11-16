package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.core.entity.SystemConfiguration;
import kh.edu.istad.iam.domain.port.out.SystemConfigurationRepository;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import kh.edu.istad.iam.restapi.dto.SystemConfigRequest;
import kh.edu.istad.iam.restapi.dto.SystemConfigResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/system/config")
@RequiredArgsConstructor
public class SystemConfigController {

    private final SystemConfigurationRepository systemConfigurationRepository;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;

    @GetMapping
    @PreAuthorize("hasAuthority('role:read') or hasRole('ADMIN')")
    public ResponseEntity<SystemConfigResponse> getConfiguration() {
        SystemConfiguration configuration = systemConfigurationRepository.getConfiguration();
        return ResponseEntity.ok(SystemConfigResponse.fromDomain(configuration));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('role:update') or hasRole('ADMIN')")
    public ResponseEntity<SystemConfigResponse> updateConfiguration(
            @Valid @RequestBody SystemConfigRequest request,
            HttpServletRequest servletRequest
    ) {
        String actor = resolveCurrentUsername();
        SystemConfiguration domain = request.toDomain(actor);
        SystemConfiguration saved = systemConfigurationRepository.saveConfiguration(domain);

        try {
            recordSecurityEventUseCase.execute(
                    null,
                    actor,
                    "ACTIVITY",
                    "SYSTEM_CONFIG_UPDATED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "System Configuration Updated",
                    "Updated authentication policies, password rules, session expiration, and token lifetimes"
            );
        } catch (Exception ex) {
            log.error("Failed to record security event for system config update: {}", ex.getMessage(), ex);
        }

        return ResponseEntity.ok(SystemConfigResponse.fromDomain(saved));
    }

    @PostMapping("/reset")
    @PreAuthorize("hasAuthority('role:update') or hasRole('ADMIN')")
    public ResponseEntity<SystemConfigResponse> resetConfiguration(HttpServletRequest servletRequest) {
        String actor = resolveCurrentUsername();
        SystemConfiguration defaults = SystemConfiguration.defaultConfiguration();
        defaults.setUpdatedBy(actor);
        SystemConfiguration saved = systemConfigurationRepository.saveConfiguration(defaults);

        try {
            recordSecurityEventUseCase.execute(
                    null,
                    actor,
                    "ACTIVITY",
                    "SYSTEM_CONFIG_RESET",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "System Configuration Reset",
                    "System security configuration restored to factory defaults"
            );
        } catch (Exception ex) {
            log.error("Failed to record security event for system config reset: {}", ex.getMessage(), ex);
        }

        return ResponseEntity.ok(SystemConfigResponse.fromDomain(saved));
    }

    private String resolveCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null && !auth.getName().isBlank()) {
            return auth.getName();
        }
        return "ADMIN";
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip != null ? ip : "127.0.0.1";
    }
}
