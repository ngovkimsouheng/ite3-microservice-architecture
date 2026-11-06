/*
package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.application.port.in.RegisterUserCommand;

import kh.edu.istad.iam.restapi.dto.RegisterUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterUserRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
                request.username(),
                request.email(),
                request.password(),
                request.confirmPassword(),
                request.givenName(),
                request.familyName(),
                request.locale()
        );

        registerUserUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}*/
