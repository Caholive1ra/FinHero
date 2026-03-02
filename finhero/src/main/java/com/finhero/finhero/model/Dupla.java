package com.finhero.finhero.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "duplas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Dupla {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, name = "user_a_id")
    private Long userAId;
    
    @Column(nullable = false, name = "user_b_id")
    private Long userBId;
    
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    public boolean containsUser(Long userId) {
        return userAId.equals(userId) || userBId.equals(userId);
    }
    
    public Long getPartnerId(Long userId) {
        if (userAId.equals(userId)) {
            return userBId;
        } else if (userBId.equals(userId)) {
            return userAId;
        }
        return null;
    }
}

