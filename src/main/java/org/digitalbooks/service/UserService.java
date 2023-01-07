package org.digitalbooks.service;

//import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.digitalbooks.entity.Role;
import org.digitalbooks.entity.User;
import org.digitalbooks.entity.security.AuthenticationResponse;
import org.digitalbooks.entity.security.LoginRequest;
import org.digitalbooks.entity.security.RegisterRequest;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.UserRepository;
import org.digitalbooks.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private static AuthenticationResponse hideUserPassword(User savedUser, String token) {
        return AuthenticationResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .name(savedUser.getName())
                .emailId(savedUser.getEmailId())
                .password("***************")
                .authorUser(savedUser.isAuthorUser())
                .build();
    }

    public Optional<User> retrieveUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> retrieveUserByEmail(String email) {
        return userRepository.findByEmailId(email);
    }

    public AuthenticationResponse addUser(RegisterRequest registerRequest) {
        userRepository.findByEmailId(registerRequest.getEmailId()).ifPresent(x -> {
            throw new UserServiceException("User Registration Error: Email Already Registered");
        });
        var user = User.builder()
                .name(registerRequest.getName())
                .emailId(registerRequest.getEmailId())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .subscriptions(new ArrayList<>())
                .authorUser(registerRequest.isAuthorUser())
                .role(registerRequest.isAuthorUser() ? Role.AUTHOR : Role.USER)
                .build();

        User newUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user.getEmailId());
        return hideUserPassword(newUser, jwtToken);
    }

    public AuthenticationResponse loginUser(LoginRequest registerRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(registerRequest.getEmailId(), registerRequest.getPassword()));
        var user = userRepository.findByEmailId(registerRequest.getEmailId()).orElseThrow(() -> new UserServiceException("Login Error: User or Password Invalid"));
        var jwtToken = jwtService.generateToken(user.getEmailId());
        return hideUserPassword(user, jwtToken);
    }
}
