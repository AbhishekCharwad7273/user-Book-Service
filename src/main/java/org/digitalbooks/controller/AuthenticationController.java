package org.digitalbooks.controller;

import lombok.RequiredArgsConstructor;
import org.digitalbooks.entity.security.AuthenticationResponse;
import org.digitalbooks.entity.security.LoginRequest;
import org.digitalbooks.entity.security.RegisterRequest;
import org.digitalbooks.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/digitalbooks/authentication/")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthenticationController {
    private final UserService userService;

    @PostMapping("sign-in")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.loginUser(loginRequest));
    }

    @PostMapping("sign-up")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(userService.addUser(registerRequest));
    }
}
