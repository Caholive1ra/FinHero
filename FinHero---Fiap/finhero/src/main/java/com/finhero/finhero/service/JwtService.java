package com.finhero.finhero.service;

import com.finhero.finhero.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    private static final long EXPIRATION_TIME = 15 * 60 * 1000; 
    
    public String generateToken(User user) {
        log.debug("Gerando token JWT para usuário: {}", user.getId());
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        String token = Jwts.builder()
            .subject(user.getEmail())
            .claim("userId", user.getId())
            .claim("email", user.getEmail())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(getSigningKey())
            .compact();
        
        log.debug("Token gerado com sucesso para usuário: {}", user.getId());
        return token;
    }
    
    public Claims validateToken(String token) {
        log.debug("Validando token JWT");
        
        try {
            Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            log.debug("Token validado com sucesso");
            return claims;
            
        } catch (Exception e) {
            log.error("Erro ao validar token: {}", e.getMessage());
            throw new RuntimeException("Token inválido ou expirado");
        }
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

