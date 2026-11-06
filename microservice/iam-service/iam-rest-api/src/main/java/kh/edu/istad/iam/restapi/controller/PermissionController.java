package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos.CreatePermissionCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.PermissionResult;
import kh.edu.istad.iam.application.usecase.permission.CreatePermissionUseCase;
import kh.edu.istad.iam.application.usecase.permission.GetPermissionUseCase;
import kh.edu.istad.iam.restapi.dto.RestDtos.CreatePermissionRequest;
import kh.edu.istad.iam.restapi.dto.RestDtos.PermissionResponse;
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

    @PostMapping
    @PreAuthorize("hasAuthority('permission:create')")
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest req) {
        PermissionResult result = createPermissionUseCase.execute(
                new CreatePermissionCommand(req.resource(), req.action(), req.description())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(new PermissionResponse(
                result.uuid(),
                result.name(),
                result.resource(),
                result.action(),
                result.description(),
                result.isSystem(),
                result.isActive()
        ));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('permission:read')")
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable UUID uuid) {
        PermissionResult result = getPermissionUseCase.execute(uuid);
        return ResponseEntity.ok(new PermissionResponse(
                result.uuid(),
                result.name(),
                result.resource(),
                result.action(),
                result.description(),
                result.isSystem(),
                result.isActive()
        ));
    }
}