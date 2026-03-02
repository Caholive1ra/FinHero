package com.finhero.finhero.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finhero.finhero.dto.LoginDTO;
import com.finhero.finhero.dto.RegisterDTO;
import com.finhero.finhero.model.User;
import com.finhero.finhero.repository.UserRepository;
import com.finhero.finhero.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    @AfterEach
    void tearDown() {
    }

    @Test
    void testRegisterEndpoint_WithValidData_ShouldReturn201() throws Exception {
        RegisterDTO dto = new RegisterDTO("newuser@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("newuser@example.com"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.inviteCode").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    void testRegisterEndpoint_WithInvalidEmail_ShouldReturn400() throws Exception {
        RegisterDTO dto = new RegisterDTO("invalid-email", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterEndpoint_WithShortPassword_ShouldReturn400() throws Exception {
        RegisterDTO dto = new RegisterDTO("test@example.com", "short");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterEndpoint_WithDuplicateEmail_ShouldReturn409() throws Exception {
        String email = "duplicate@example.com";
        
        User existingUser = new User();
        existingUser.setEmail(email);
        existingUser.setPasswordHash(passwordEncoder.encode("password123"));
        existingUser.setInviteCode("EXIST001");
        existingUser.setCreatedAt(LocalDateTime.now());
        existingUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(existingUser);

        RegisterDTO dto = new RegisterDTO(email, "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict());
    }

    @Test
    void testLoginEndpoint_WithValidCredentials_ShouldReturn200() throws Exception {
        String email = "login@example.com";
        String password = "password123";
        
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setInviteCode("LOGIN001");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginDTO dto = new LoginDTO(email, password);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    void testLoginEndpoint_WithInvalidEmail_ShouldReturn401() throws Exception {
        LoginDTO dto = new LoginDTO("nonexistent@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginEndpoint_WithInvalidPassword_ShouldReturn401() throws Exception {
        String email = "wrongpass@example.com";
        String correctPassword = "password123";
        
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(correctPassword));
        user.setInviteCode("WRONG001");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginDTO dto = new LoginDTO(email, "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRegisterEndpoint_EmailNormalization_ShouldWork() throws Exception {
        RegisterDTO dto = new RegisterDTO("UPPERCASE@EXAMPLE.COM", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("uppercase@example.com"));
    }

    @Test
    void testLoginEndpoint_EmailNormalization_ShouldWork() throws Exception {
        String email = "lowercase@example.com";
        String password = "password123";
        
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setInviteCode("LOWER001");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        LoginDTO dto = new LoginDTO("LOWERCASE@EXAMPLE.COM", password);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}


