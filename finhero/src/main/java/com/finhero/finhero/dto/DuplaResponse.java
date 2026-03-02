package com.finhero.finhero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplaResponse {
    
    private Long id;
    private Long userAId;
    private Long userBId;
    private LocalDateTime createdAt;
}

