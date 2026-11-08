package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos.CreatePermissionCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UpdatePermissionCommand;
import kh.edu.istad.iam.application.usecase.permission.*;
import kh.edu.istad.iam.restapi.dto.RestDtos.CreatePermissionRequest;
import kh.edu.istad.iam.restapi.dto.RestDtos.PermissionResponse;
import kh.edu.istad.iam.restapi.dto.RestDtos.UpdatePermissionRequest;
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

    // POST /api/permissions
    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest req) {
        PermissionResult result = createPermissionUseCase.execute(
                new CreatePermissionCommand(req.resource(), req.action(), req.description())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(result));
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
        return ResponseEntity.ok(new PageResult<>(
                result.content().stream().map(this::toResponse).toList(),
                result.pageNumber(),
                result.pageSize(),
                result.totalElements(),
                result.totalPages()
        ));
    }

    // GET /api/permissions/{uuid}
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable("uuid") UUID uuid) {
        PermissionResult result = getPermissionUseCase.execute(uuid);
        return ResponseEntity.ok(toResponse(result));
    }

    // PATCH /api/permissions/{uuid}
    @PatchMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:update')")
    public ResponseEntity<PermissionResponse> updatePermission(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody UpdatePermissionRequest req
    ) {
        PermissionResult result = updatePermissionUseCase.execute(
                new UpdatePermissionCommand(uuid, req.description())
        );
        return ResponseEntity.ok(toResponse(result));
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

    private PermissionResponse toResponse(PermissionResult r) {
        return new PermissionResponse(
                r.uuid(),
                r.name(),
                r.resource(),
                r.action(),
                r.description(),
                r.isSystem(),
                r.active()
        );
    }
}