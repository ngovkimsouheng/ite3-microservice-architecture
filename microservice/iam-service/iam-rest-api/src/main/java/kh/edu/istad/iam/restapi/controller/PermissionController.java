package kh.edu.istad.iam.restapi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.usecase.permission.*;
import kh.edu.istad.iam.domain.usecase.security.RecordSecurityEventUseCase;
import kh.edu.istad.iam.restapi.dto.CreatePermissionRequest;
import kh.edu.istad.iam.restapi.dto.PermissionResponse;
import kh.edu.istad.iam.restapi.dto.UpdatePermissionRequest;
import kh.edu.istad.iam.restapi.mapper.IamWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final CreatePermissionUseCase createPermissionUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final ListPermissionsUseCase listPermissionsUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final ActivatePermissionUseCase activatePermissionUseCase;
    private final DeactivatePermissionUseCase deactivatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final RecordSecurityEventUseCase recordSecurityEventUseCase;
    private final IamWebMapper webMapper;

    // POST /api/permissions
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest req, HttpServletRequest servletRequest) {
        PermissionResult result = createPermissionUseCase.execute(webMapper.toCommand(req));
        try {
            recordSecurityEventUseCase.execute(
                    null,
                    "admin",
                    "ACTIVITY",
                    "PERMISSION_CREATED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "Permission Created",
                    "Created permission: " + result.name() + " (" + result.resource() + ":" + result.action() + ")"
            );
        } catch (Exception ignored) {}
        return ResponseEntity.status(HttpStatus.CREATED).body(webMapper.toPermissionResponse(result));
    }

    // GET /api/permissions
    @GetMapping
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PageResult<PermissionResponse>> listPermissions(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        PageResult<PermissionResult> result = listPermissionsUseCase.execute(page, size, sort);
        return ResponseEntity.ok(webMapper.toPermissionPageResult(result));
    }

    // GET /api/permissions/{uuid}
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable("uuid") UUID uuid) {
        PermissionResult result = getPermissionUseCase.execute(uuid);
        return ResponseEntity.ok(webMapper.toPermissionResponse(result));
    }

    // PATCH /api/permissions/{uuid}
    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<PermissionResponse> updatePermission(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody UpdatePermissionRequest req
    ) {
        PermissionResult result = updatePermissionUseCase.execute(webMapper.toCommand(uuid, req));
        return ResponseEntity.ok(webMapper.toPermissionResponse(result));
    }

    // POST /api/permissions/{uuid}/activate
    @PostMapping("/{uuid}/activate")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<Void> activatePermission(@PathVariable("uuid") UUID uuid) {
        activatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    // POST /api/permissions/{uuid}/deactivate
    @PostMapping("/{uuid}/deactivate")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<Void> deactivatePermission(@PathVariable("uuid") UUID uuid) {
        deactivatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    // DELETE /api/permissions/{uuid}
    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:delete')")
    public ResponseEntity<Void> deletePermission(@PathVariable("uuid") UUID uuid, HttpServletRequest servletRequest) {
        String permName = uuid.toString();
        try {
            PermissionResult p = getPermissionUseCase.execute(uuid);
            permName = p.name();
        } catch (Exception ignored) {}
        deletePermissionUseCase.execute(uuid);
        try {
            recordSecurityEventUseCase.execute(
                    null,
                    "admin",
                    "ACTIVITY",
                    "PERMISSION_DELETED",
                    "SUCCESS",
                    extractClientIp(servletRequest),
                    servletRequest.getHeader("User-Agent"),
                    "Phnom Penh, Cambodia",
                    null,
                    "Permission Deleted",
                    "Deleted permission: " + permName
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
