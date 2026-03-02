package com.finhero.finhero.service;

import com.finhero.finhero.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private static final String SECRET_KEY = "MinhaChaveSecretaSuperForteCom256BitsParaJWTTokenSecurity2025FinHero";
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setInviteCode("ABC12345");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testGenerateToken_ShouldCreateValidToken() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    void testValidateToken_WithValidToken_ShouldReturnClaims() {
        String token = jwtService.generateToken(user);
        Claims claims = jwtService.validateToken(token);

        assertNotNull(claims);
        assertEquals("test@example.com", claims.getSubject());
        assertEquals(1, claims.get("userId"));
        assertEquals("test@example.com", claims.get("email"));
    }

    @Test
    void testValidateToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid.token.here";

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            jwtService.validateToken(invalidToken);
        });

        assertEquals("Token inválido ou expirado", exception.getMessage());
    }

    @Test
    void testGenerateAndValidateToken_DifferentUsers_ShouldHaveDifferentTokens() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("test2@example.com");
        user2.setPasswordHash("hashedPassword");
        user2.setInviteCode("XYZ67890");
        user2.setCreatedAt(LocalDateTime.now());
        user2.setUpdatedAt(LocalDateTime.now());

        String token1 = jwtService.generateToken(user);
        String token2 = jwtService.generateToken(user2);

        assertNotEquals(token1, token2);

        Claims claims1 = jwtService.validateToken(token1);
        Claims claims2 = jwtService.validateToken(token2);

        assertEquals("test@example.com", claims1.getSubject());
        assertEquals("test2@example.com", claims2.getSubject());
    }

    @Test
    void testGenerateToken_MultipleTimes_ShouldCreateDifferentTokens() {
        String token1 = jwtService.generateToken(user);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String token2 = jwtService.generateToken(user);

        assertNotNull(token1);
        assertNotNull(token2);
        
        Claims claims1 = jwtService.validateToken(token1);
        Claims claims2 = jwtService.validateToken(token2);

        assertEquals(claims1.getSubject(), claims2.getSubject());
        assertEquals(claims1.get("userId"), claims2.get("userId"));
    }
}

