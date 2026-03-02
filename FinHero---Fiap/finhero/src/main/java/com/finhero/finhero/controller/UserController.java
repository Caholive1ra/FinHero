package com.finhero.finhero.controller;

import com.finhero.finhero.dto.UserResponse;
import com.finhero.finhero.model.User;
import com.finhero.finhero.repository.UserRepository;
import com.finhero.finhero.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Long userId = SecurityUtil.getCurrentUserId();
        
        if (userId == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Não autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        
        User user = userRepository.findById(userId)
            .orElse(null);
        
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Usuário não encontrado");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
        
        UserResponse response = new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getInviteCode(),
            user.getCreatedAt()
        );
        
        return ResponseEntity.ok(response);
    }
}

