package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.dto.PageResult;
import kh.edu.istad.iam.domain.dto.PermissionResult;
import kh.edu.istad.iam.domain.usecase.permission.*;
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
    private final IamWebMapper webMapper;

    // POST /api/permissions
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest req) {
        PermissionResult result = createPermissionUseCase.execute(webMapper.toCommand(req));
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
    public ResponseEntity<Void> deletePermission(@PathVariable("uuid") UUID uuid) {
        deletePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

}
