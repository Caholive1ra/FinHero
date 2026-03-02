package com.finhero.finhero.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkDuplaDTO {
    
    @NotBlank(message = "Código de convite é obrigatório")
    private String inviteCode;
}

