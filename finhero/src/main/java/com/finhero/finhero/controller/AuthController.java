package com.finhero.finhero.controller;

import com.finhero.finhero.dto.AuthResponse;
import com.finhero.finhero.dto.LoginDTO;
import com.finhero.finhero.dto.RegisterDTO;
import com.finhero.finhero.dto.UserResponse;
import com.finhero.finhero.model.User;
import com.finhero.finhero.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO dto) {
        log.info("Recebida requisição de cadastro para email: {}", dto.getEmail());
        
        User user = authService.register(dto.getEmail(), dto.getPassword());
        
        UserResponse response = new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getInviteCode(),
            user.getCreatedAt()
        );
        
        log.info("Cadastro realizado com sucesso: id={}", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO dto) {
        log.info("Recebida requisição de login para email: {}", dto.getEmail());
        
        AuthResponse response = authService.login(dto.getEmail(), dto.getPassword());
        
        log.info("Login realizado com sucesso");
        return ResponseEntity.ok(response);
    }
}

