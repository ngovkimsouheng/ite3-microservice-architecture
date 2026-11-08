package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos;
import kh.edu.istad.iam.application.dto.CommonDtos.CreateRoleCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PageResult;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleSummary;
import kh.edu.istad.iam.application.dto.CommonDtos.UpdateRoleCommand;
import kh.edu.istad.iam.application.usecase.role.*;
import kh.edu.istad.iam.restapi.dto.RestDtos;
import kh.edu.istad.iam.restapi.dto.RestDtos.AssignPermissionsToRoleRequest;
import kh.edu.istad.iam.restapi.dto.RestDtos.CreateRoleRequest;
import kh.edu.istad.iam.restapi.dto.RestDtos.RoleResponse;
import kh.edu.istad.iam.restapi.dto.RestDtos.RoleSummaryResponse;
import kh.edu.istad.iam.restapi.dto.RestDtos.UpdateRoleRequest;
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

    @GetMapping
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RestDtos.PageResponse<RoleSummaryResponse>> listRoles(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", required = false) String sort
    ) {
        CommonDtos.PageResult<CommonDtos.RoleSummary> result = listRolesUseCase.execute(page, size, sort);
        List<RoleSummaryResponse> content = result.content().stream()
                .map(r -> new RoleSummaryResponse(r.uuid(), r.name(), r.active()))
                .toList();

        return ResponseEntity.ok(new RestDtos.PageResponse<>(
                content,
                new RestDtos.PageMetadata(result.pageNumber(), result.pageSize(), result.totalElements(), result.totalPages())
        ));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest req) {
        RoleResult result = createRoleUseCase.execute(new CreateRoleCommand(req.name(), req.description()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RoleResponse(
                result.uuid(),
                result.name(),
                result.description(),
                result.isSystem(),
                result.active()
        ));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleResponse> getRole(@PathVariable("uuid") UUID uuid) {
        RoleResult result = getRoleUseCase.execute(uuid);
        return ResponseEntity.ok(new RoleResponse(
                result.uuid(),
                result.name(),
                result.description(),
                result.isSystem(),
                result.active()
        ));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:update')")
    public ResponseEntity<RoleResponse> updateRole(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody UpdateRoleRequest req
    ) {
        RoleResult result = updateRoleUseCase.execute(new UpdateRoleCommand(uuid, req.name(), req.description()));
        return ResponseEntity.ok(new RoleResponse(
                result.uuid(),
                result.name(),
                result.description(),
                result.isSystem(),
                result.active()
        ));
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
    public ResponseEntity<Void> deleteRole(@PathVariable("uuid") UUID uuid) {
        deleteRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/permissions")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<List<CommonDtos.PermissionAssignmentResult>> assignPermissions(
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody AssignPermissionsToRoleRequest req
    ) {
        List<CommonDtos.PermissionAssignmentResult> results = assignPermissionsToRoleUseCase.execute(
                new CommonDtos.AssignPermissionsToRoleCommand(uuid, req.permissionUuids(), req.reason())
        );
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{uuid}/permissions/{permUuid}")
    @PreAuthorize("hasAuthority('role:assign')")
    public ResponseEntity<Void> revokePermission(
            @PathVariable("uuid") UUID uuid,
            @PathVariable("permUuid") UUID permUuid,
            @RequestParam(name = "reason", required = false) String reason
    ) {
        revokePermissionFromRoleUseCase.execute(uuid, permUuid, reason);
        return ResponseEntity.noContent().build();
    }
}