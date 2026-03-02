package com.finhero.finhero.service;

import com.finhero.finhero.dto.AuthResponse;
import com.finhero.finhero.dto.UserResponse;
import com.finhero.finhero.exception.EmailAlreadyExistsException;
import com.finhero.finhero.exception.InvalidCredentialsException;
import com.finhero.finhero.model.Category;
import com.finhero.finhero.model.User;
import com.finhero.finhero.repository.CategoryRepository;
import com.finhero.finhero.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private JwtService jwtService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    
    private static final List<String> CATEGORIAS_PADRAO = Arrays.asList(
        "Moradia", "Alimentação", "Transporte", 
        "Saúde", "Educação", "Lazer", "Outros"
    );
    
    @Transactional
    public User register(String email, String password) {
        log.info("Iniciando cadastro de usuário com email: {}", email);
        
        String normalizedEmail = email.toLowerCase();
        
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            log.warn("Tentativa de cadastro com e-mail já existente: {}", normalizedEmail);
            throw new EmailAlreadyExistsException("E-mail já cadastrado");
        }
        
        
        String passwordHash = passwordEncoder.encode(password);
        
        String inviteCode = generateUniqueInviteCode();
        
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPasswordHash(passwordHash);
        user.setInviteCode(inviteCode);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        
        log.info("Usuário criado com sucesso: id={}, email={}, inviteCode={}", 
                 savedUser.getId(), savedUser.getEmail(), savedUser.getInviteCode());
        
        createDefaultCategories(savedUser);
        
        return savedUser;
    }
    
    private String generateUniqueInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        String code;
        int maxAttempts = 100;
        int attempts = 0;
        
        do {
            StringBuilder sb = new StringBuilder(8);
            for (int i = 0; i < 8; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            code = sb.toString();
            attempts++;
            
            if (attempts > maxAttempts) {
                log.error("Não foi possível gerar código único após {} tentativas", maxAttempts);
                throw new RuntimeException("Erro ao gerar código de convite");
            }
        } while (userRepository.findByInviteCode(code).isPresent());
        
        return code;
    }
    
    @Transactional
    private void createDefaultCategories(User user) {
        log.info("Criando categorias padrão para usuário: {}", user.getId());
        
        for (String nome : CATEGORIAS_PADRAO) {
            Category category = new Category();
            category.setName(nome);
            category.setUserId(user.getId());
            category.setCreatedAt(LocalDateTime.now());
            categoryRepository.save(category);
        }
        
        log.info("{} categorias criadas para usuário: {}", CATEGORIAS_PADRAO.size(), user.getId());
    }
    
    public AuthResponse login(String email, String password) {
        log.info("Iniciando login para email: {}", email);
        
        String normalizedEmail = email.toLowerCase();
        
        User user = userRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> {
                log.warn("Tentativa de login com e-mail não encontrado: {}", normalizedEmail);
                throw new InvalidCredentialsException("Credenciais inválidas");
            });
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.warn("Tentativa de login com senha incorreta para: {}", normalizedEmail);
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
        
        String token = jwtService.generateToken(user);
        
        UserResponse userResponse = new UserResponse(
            user.getId(),
            user.getEmail(),
            user.getInviteCode(),
            user.getCreatedAt()
        );
        
        log.info("Login bem-sucedido para usuário: {}", user.getId());
        return new AuthResponse(token, userResponse);
    }
}

