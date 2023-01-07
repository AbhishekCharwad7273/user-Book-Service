package org.digitalbooks.service;

import org.digitalbooks.entity.User;
import org.digitalbooks.entity.security.AuthenticationResponse;
import org.digitalbooks.entity.security.LoginRequest;
import org.digitalbooks.entity.security.RegisterRequest;
import org.digitalbooks.exception.UserServiceException;
import org.digitalbooks.repository.UserRepository;
import org.digitalbooks.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@SpringBootTest
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @Mock
    AuthenticationManager authenticationManager;

    UserService userService;

    @BeforeEach
    public final void setup() {
        userService = new UserService(userRepository, new BCryptPasswordEncoder(), new JwtService(), authenticationManager);
    }

    @Test
    void addUser_ValidTest() {
        JwtService jwtService = new JwtService();
        //Expected
        User expectedUser = User.builder()
                .id(1L)
                .password("qwerty")
                .emailId("test@gmail.com")
                .name("Tester One")
                .authorUser(true)
                .build();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .emailId("test@gmail.com")
                .password("qwerty")
                .name("Tester One")
                .authorUser(true)
                .build();

        //Mocks
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

        //When
        AuthenticationResponse response = userService.addUser(registerRequest);

        //Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo(registerRequest.getName());
        assertThat(response.getEmailId()).isEqualTo(registerRequest.getEmailId());
        assertThat(response.getPassword()).isEqualTo("***************");
        assertThat(response.getToken()).isEqualTo(jwtService.generateToken(registerRequest.getEmailId()));

    }

    @Test
    void addUser_InValidTest() {
        //Expected
        String errorMsg = "User Registration Error: Email Already Registered";
        User expectedUser = User.builder()
                .id(1L)
                .password("qwerty")
                .emailId("test@gmail.com")
                .name("Tester One")
                .authorUser(true)
                .build();

        RegisterRequest registerRequest = RegisterRequest.builder()
                .emailId("test@gmail.com")
                .password("qwerty")
                .name("Tester One")
                .authorUser(true)
                .build();

        //Mocks
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(expectedUser));

        //Then
        assertThatThrownBy(() -> userService.addUser(registerRequest), errorMsg)
                .isInstanceOf(UserServiceException.class);
    }

    @Test
    void loginUser_ValidTest() {
        JwtService jwtService = new JwtService();
        //Expected
        User expectedUser = User.builder()
                .id(1L)
                .password("qwerty")
                .emailId("test@gmail.com")
                .name("Tester One")
                .authorUser(true)
                .build();

        LoginRequest registerRequest = LoginRequest.builder()
                .emailId("test@gmail.com")
                .password("qwerty")
                .build();

        //Mocks
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.of(expectedUser));
        Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

        //When
        AuthenticationResponse response = userService.loginUser(registerRequest);

        //Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getEmailId()).isEqualTo(registerRequest.getEmailId());
        assertThat(response.getPassword()).isEqualTo("***************");
        assertThat(response.getToken()).isEqualTo(jwtService.generateToken(registerRequest.getEmailId()));
    }

    @Test
    void loginUser_InValidTest() {
        String errorMsg = "Login Error: User or Password Invalid";
        //Expected
        User expectedUser = User.builder()
                .id(1L)
                .password("qwerty")
                .emailId("test@gmail.com")
                .name("Tester One")
                .authorUser(true)
                .build();

        LoginRequest registerRequest = LoginRequest.builder()
                .emailId("test@gmail.com")
                .password("qwerty")
                .build();

        //Mocks
        Mockito.when(userRepository.findByEmailId(anyString())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any())).thenReturn(expectedUser);

        //Then
        assertThatThrownBy(() -> userService.loginUser(registerRequest), errorMsg).isInstanceOf(UserServiceException.class);
    }
}