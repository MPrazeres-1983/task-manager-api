package com.mprazeres.taskmanager.service;

import com.mprazeres.taskmanager.dto.auth.LoginRequest;
import com.mprazeres.taskmanager.dto.auth.RegisterRequest;
import com.mprazeres.taskmanager.exception.ConflictException;
import com.mprazeres.taskmanager.model.User;
import com.mprazeres.taskmanager.repository.UserRepository;
import com.mprazeres.taskmanager.security.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Register — should create user and return token")
    void register_shouldCreateUserAndReturnToken() {
        RegisterRequest request = new RegisterRequest("Mário", "mario@test.com", "password123");

        when(userRepository.existsByEmail("mario@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserDetails ud = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("mario@test.com")).thenReturn(ud);
        when(jwtService.generateToken(ud)).thenReturn("jwt-token");

        var response = authService.register(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("mario@test.com");
        assertThat(response.name()).isEqualTo("Mário");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register — should throw ConflictException if email exists")
    void register_shouldThrowIfEmailExists() {
        RegisterRequest request = new RegisterRequest("Mário", "exists@test.com", "password123");
        when(userRepository.existsByEmail("exists@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("exists@test.com");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login — should return token for valid credentials")
    void login_shouldReturnTokenForValidCredentials() {
        LoginRequest request = new LoginRequest("mario@test.com", "password123");
        User user = User.builder().id(1L).name("Mário").email("mario@test.com")
                .password("hashed").build();

        when(userRepository.findByEmail("mario@test.com")).thenReturn(Optional.of(user));
        UserDetails ud = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("mario@test.com")).thenReturn(ud);
        when(jwtService.generateToken(ud)).thenReturn("jwt-token");

        var response = authService.login(request);

        assertThat(response.token()).isEqualTo("jwt-token");
        assertThat(response.email()).isEqualTo("mario@test.com");
    }

    @Test
    @DisplayName("Login — should throw BadCredentialsException for wrong password")
    void login_shouldThrowForWrongPassword() {
        LoginRequest request = new LoginRequest("mario@test.com", "wrong");
        doThrow(new BadCredentialsException("Bad credentials"))
                .when(authenticationManager).authenticate(any());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }
}
