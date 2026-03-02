package com.finhero.finhero.service;

import com.finhero.finhero.dto.AuthResponse;
import com.finhero.finhero.exception.EmailAlreadyExistsException;
import com.finhero.finhero.exception.InvalidCredentialsException;
import com.finhero.finhero.model.Category;
import com.finhero.finhero.model.User;
import com.finhero.finhero.repository.CategoryRepository;
import com.finhero.finhero.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_INVITE_CODE = "ABC12345";

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail(TEST_EMAIL);
        user.setPasswordHash(new BCryptPasswordEncoder(10).encode(TEST_PASSWORD));
        user.setInviteCode(TEST_INVITE_CODE);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testRegister_WithValidData_ShouldCreateUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category());

        User result = authService.register(TEST_EMAIL, TEST_PASSWORD);

        assertNotNull(result);
        assertEquals(TEST_EMAIL, result.getEmail());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(userRepository, times(1)).save(any(User.class));
        verify(categoryRepository, times(7)).save(any(Category.class));
    }

    @Test
    void testRegister_WithExistingEmail_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> authService.register(TEST_EMAIL, TEST_PASSWORD)
        );

        assertEquals("E-mail já cadastrado", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegister_ShouldNormalizeEmailToLowerCase() {
        String uppercaseEmail = "TEST@EXAMPLE.COM";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category());

        User result = authService.register(uppercaseEmail, TEST_PASSWORD);

        assertEquals(TEST_EMAIL, result.getEmail());
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturnAuthResponse() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(TEST_EMAIL, TEST_PASSWORD);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertNotNull(response.getUser());
        verify(userRepository, times(1)).findByEmail(anyString());
        verify(jwtService, times(1)).generateToken(any(User.class));
    }

    @Test
    void testLogin_WithInvalidEmail_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(
            InvalidCredentialsException.class,
            () -> authService.login(TEST_EMAIL, TEST_PASSWORD)
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void testLogin_WithInvalidPassword_ShouldThrowException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        assertThrows(
            InvalidCredentialsException.class,
            () -> authService.login(TEST_EMAIL, "wrongpassword")
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void testLogin_ShouldNormalizeEmailToLowerCase() {
        String uppercaseEmail = "TEST@EXAMPLE.COM";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        AuthResponse response = authService.login(uppercaseEmail, TEST_PASSWORD);

        assertNotNull(response);
        verify(userRepository).findByEmail(TEST_EMAIL);
    }

    @Test
    void testRegister_ShouldGenerateUniqueInviteCode() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByInviteCode(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(new Category());

        User result = authService.register("newuser@example.com", TEST_PASSWORD);

        assertNotNull(result);
        verify(userRepository, atLeastOnce()).findByInviteCode(anyString());
    }
}

