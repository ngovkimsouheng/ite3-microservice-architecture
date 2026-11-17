package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.dto.*;
import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.RoleResult;
import kh.edu.istad.iam.domain.dto.RoleSummary;
import kh.edu.istad.iam.domain.usecase.role.*;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import kh.edu.istad.iam.restapi.dto.*;
import kh.edu.istad.iam.restapi.mapper.IamWebMapper;
import kh.edu.istad.iam.restapi.dto.AssignPermissionsToRoleRequest;
import kh.edu.istad.iam.restapi.dto.CreateRoleRequest;
import kh.edu.istad.iam.restapi.dto.RoleResponse;
import kh.edu.istad.iam.restapi.dto.RoleSummaryResponse;
import kh.edu.istad.iam.restapi.dto.UpdateRoleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final ActivateRoleUseCase activateRoleUseCase;
    private final DeactivateRoleUseCase deactivateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;
    private final RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase;
    private final GetRolePermissionsUseCase getRolePermissionsUseCase;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;
    private final IamWebMapper webMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<PageResponse<RoleSummaryResponse>> listRoles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        PageResult<RoleSummary> result = listRolesUseCase.execute(page, size, sort);
        return ResponseEntity.ok(webMapper.toRolePageResponse(result));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest req, HttpServletRequest servletRequest) {
        RoleResult result = createRoleUseCase.execute(webMapper.toCommand(req));
        try {
            recordSecurityEventUseCase.execute(
                    null,
                    "admin",
                    "ACTIVITY",
                    "ROLE_CREATED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "Role Created",
                    "Created new role: " + result.name()
            );
        } catch (Exception ignored) {}
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toRoleResponse(result));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleResponse> getRole(@PathVariable("uuid") UUID uuid) {
        RoleResult result = getRoleUseCase.execute(uuid);
        return ResponseEntity.ok(webMapper.toRoleResponse(result));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody UpdateRoleRequest req
    ) {
        RoleResult result = updateRoleUseCase.execute(webMapper.toCommand(uuid, req));
        return ResponseEntity.ok(webMapper.toRoleResponse(result));
    }

    @PostMapping("/{uuid}/activate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> activateRole(@PathVariable("uuid") UUID uuid) {
        activateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<Void> deactivateRole(@PathVariable("uuid") UUID uuid) {
        deactivateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:delete')")
    public ResponseEntity<Void> deleteRole(@PathVariable("uuid") UUID uuid, HttpServletRequest servletRequest) {
        String roleName = uuid.toString();
        try {
            RoleResult r = getRoleUseCase.execute(uuid);
            roleName = r.name();
        } catch (Exception ignored) {}
        deleteRoleUseCase.execute(uuid);
        try {
            recordSecurityEventUseCase.execute(
                    null,
                    "admin",
                    "ACTIVITY",
                    "ROLE_DELETED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "Role Deleted",
                    "Deleted role: " + roleName
            );
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<List<PermissionResponse>> getRolePermissions(@PathVariable("uuid") UUID uuid) {
        List<PermissionResult> results = getRolePermissionsUseCase.execute(uuid);
        List<PermissionResponse> response = results.stream()
                .map(webMapper::toPermissionResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<List<PermissionAssignmentResult>> assignPermissions(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody AssignPermissionsToRoleRequest req,
            HttpServletRequest servletRequest
    ) {
        List<PermissionAssignmentResult> results =
                assignPermissionsToRoleUseCase.execute(webMapper.toCommand(uuid, req));
        try {
            RoleResult r = getRoleUseCase.execute(uuid);
            recordSecurityEventUseCase.execute(
                    null,
                    "admin",
                    "ACTIVITY",
                    "ROLE_PERMISSION_ASSIGNED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "Role Permissions Modified",
                    "Assigned " + results.size() + " permission(s) to role: " + r.name()
            );
        } catch (Exception ignored) {}
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{uuid}/permissions/{permUuid}")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<Void> revokePermission(
            @PathVariable("uuid") UUID uuid,
            @PathVariable("permUuid") UUID permUuid,
            @RequestParam(name = "reason", required = false) String reason,
            HttpServletRequest servletRequest
    ) {
        revokePermissionFromRoleUseCase.execute(uuid, permUuid, reason);
        try {
            RoleResult r = getRoleUseCase.execute(uuid);
            recordSecurityEventUseCase.execute(
                    null,
                    "admin",
                    "ACTIVITY",
                    "ROLE_PERMISSION_REVOKED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "Role Permissions Modified",
                    "Revoked permission from role: " + r.name() + (reason != null ? " (" + reason + ")" : "")
            );
        } catch (Exception ignored) {}
        return ResponseEntity.noContent().build();
    }

    private String extractClientIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1 (Localhost)";
        String ip = null;
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank() && !"unknown".equalsIgnoreCase(xf)) {
            ip = xf.split(",")[0].trim();
        }
        if (ip == null || ip.isBlank()) {
            String xr = request.getHeader("X-Real-IP");
            if (xr != null && !xr.isBlank() && !"unknown".equalsIgnoreCase(xr)) {
                ip = xr.trim();
            }
        }
        if (ip == null || ip.isBlank()) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1") || ip.equals("127.0.0.1")) {
            return "127.0.0.1 (Localhost)";
        }
        return ip.trim();
    }
}
