package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos.UserAuthoritiesResult;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserAuthoritiesUseCase;
import kh.edu.istad.iam.application.usecase.user.GetUserUseCase;
import kh.edu.istad.iam.restapi.dto.RestDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final GetUserUseCase getUserUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;

    @GetMapping("/users/{uuid}")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserResponse> getUserByUuid(@PathVariable("uuid") UUID uuid) {
        UserResult result = getUserUseCase.execute(uuid);
        return ResponseEntity.ok(new UserResponse(
                result.uuid(),
                result.username(),
                result.email(),
                result.emailVerified(),
                result.givenName() + " " + result.familyName(),
                result.locale(),
                result.enabled(),
                Collections.emptyList(),
                result.createdAt(),
                result.updatedAt()
        ));
    }

    @GetMapping("/users/{uuid}/authorities")
    @PreAuthorize("hasAuthority('user:read')")
    public ResponseEntity<UserAuthoritiesResponse> getUserAuthorities(@PathVariable UUID uuid) {
        UserAuthoritiesResult result = getUserAuthoritiesUseCase.execute(uuid);
        return ResponseEntity.ok(new UserAuthoritiesResponse(
                result.roles(),
                result.permissions()
        ));
    }

    @DeleteMapping("/users/{uuid}/identities/{identityUuid}")
    @PreAuthorize("hasAuthority('user:update')")
    public ResponseEntity<Void> unlinkIdentity(
            @PathVariable UUID uuid,
            @PathVariable UUID identityUuid
    ) {
        unlinkIdentityUseCase.execute(uuid, identityUuid);
        return ResponseEntity.noContent().build();
    }
}