package kg.infosystems.statefin.controller;

import kg.infosystems.statefin.dto.request.LoginRequest;
import kg.infosystems.statefin.dto.request.RefreshTokenRequest;
import kg.infosystems.statefin.dto.request.UserRegistrationRequest;
import kg.infosystems.statefin.dto.response.ApiResponse;
import kg.infosystems.statefin.dto.response.LoginResponse;
import kg.infosystems.statefin.dto.response.UserResponse;
import kg.infosystems.statefin.service.AuthService;
import kg.infosystems.statefin.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", loginResponse));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        LoginResponse loginResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", loginResponse));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody UserRegistrationRequest registrationRequest) {
        UserResponse userResponse = userService.createUser(registrationRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", userResponse));
    }

}