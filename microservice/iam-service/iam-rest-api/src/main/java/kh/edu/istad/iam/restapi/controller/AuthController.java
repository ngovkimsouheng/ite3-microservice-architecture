package kh.edu.istad.iam.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.istad.iam.domain.dto.UserResult;
import kh.edu.istad.iam.domain.usecase.user.RegisterUserUseCase;
import kh.edu.istad.iam.restapi.dto.RegisterUserRequest;
import kh.edu.istad.iam.restapi.dto.UserResponse;
import kh.edu.istad.iam.restapi.mapper.IamWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final IamWebMapper webMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest req) {
        UserResult result = registerUserUseCase.execute(webMapper.toCommand(req));
        UserResponse response = webMapper.toUserResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
