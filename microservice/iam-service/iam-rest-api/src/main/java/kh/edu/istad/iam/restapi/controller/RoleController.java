package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos.CreateRoleCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.RoleResult;
import kh.edu.istad.iam.application.usecase.role.CreateRoleUseCase;
import kh.edu.istad.iam.application.usecase.role.GetRoleUseCase;
import kh.edu.istad.iam.restapi.dto.RestDtos.CreateRoleRequest;
import kh.edu.istad.iam.restapi.dto.RestDtos.RoleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final GetRoleUseCase getRoleUseCase;

    @PostMapping
    @PreAuthorize("hasAuthority('role:create')")
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest req) {
        RoleResult result = createRoleUseCase.execute(new CreateRoleCommand(req.name(), req.description()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new RoleResponse(
                result.uuid(),
                result.name(),
                result.description(),
                result.isSystem(),
                result.isActive()
        ));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAuthority('role:read')")
    public ResponseEntity<RoleResponse> getRole(@PathVariable UUID uuid) {
        RoleResult result = getRoleUseCase.execute(uuid);
        return ResponseEntity.ok(new RoleResponse(
                result.uuid(),
                result.name(),
                result.description(),
                result.isSystem(),
                result.isActive()
        ));
    }
}