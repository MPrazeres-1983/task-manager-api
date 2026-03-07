package com.mprazeres.taskmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey",
                "TestSecretKeyForJWTThatMustBeAtLeast256BitsLongForTesting!!");
        ReflectionTestUtils.setField(jwtService, "expirationMs", 3600000L);
        ReflectionTestUtils.invokeMethod(jwtService, "validateConfiguration");

        userDetails = User.builder()
                .username("mario@example.com")
                .password("encodedPassword")
                .roles("USER")
                .build();
    }

    @Test
    @DisplayName("Should generate a non-null JWT token")
    void shouldGenerateToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(token).isNotNull().isNotEmpty();
    }

    @Test
    @DisplayName("Should extract correct username from token")
    void shouldExtractUsername() {
        String token = jwtService.generateToken(userDetails);
        String extracted = jwtService.extractUsername(token);
        assertThat(extracted).isEqualTo("mario@example.com");
    }

    @Test
    @DisplayName("Should validate a valid token")
    void shouldValidateValidToken() {
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }

    @Test
    @DisplayName("Should reject token for different user")
    void shouldRejectTokenForDifferentUser() {
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = User.builder()
                .username("other@example.com")
                .password("pass")
                .roles("USER")
                .build();

        assertThat(jwtService.isTokenValid(token, otherUser)).isFalse();
    }

    @Test
    @DisplayName("Should reject expired token")
    void shouldRejectExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expirationMs", -1000L);
        String token = jwtService.generateToken(userDetails);
        assertThat(jwtService.isTokenValid(token, userDetails)).isFalse();
    }

    @Test
    @DisplayName("Should reject malformed token")
    void shouldRejectMalformedToken() {
        assertThat(jwtService.isTokenValid("not-a-jwt", userDetails)).isFalse();
    }

    @Test
    @DisplayName("Should fail fast when secret is too short")
    void shouldFailWhenSecretIsTooShort() {
        JwtService invalidService = new JwtService();
        ReflectionTestUtils.setField(invalidService, "secretKey", "short-secret");

        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(invalidService, "validateConfiguration"))
                .hasMessageContaining("JWT secret must be at least 32 bytes long");
    }
}
