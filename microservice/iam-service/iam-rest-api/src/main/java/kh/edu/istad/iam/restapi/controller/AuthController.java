package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.dto.CommonDtos.RegisterUserCommand;
import kh.edu.istad.iam.application.dto.CommonDtos.UserResult;
import kh.edu.istad.iam.application.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.restapi.dto.RestDtos.RegisterUserRequest;
import kh.edu.istad.iam.restapi.dto.RestDtos.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest req) {
        RegisterUserCommand cmd = new RegisterUserCommand(
                req.username(),
                req.email(),
                req.password(),
                req.confirmPassword(),
                req.givenName(),
                req.familyName(),
                req.locale()
        );

        UserResult result = registerUserUseCase.execute(cmd);

        UserResponse response = new UserResponse(
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
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}