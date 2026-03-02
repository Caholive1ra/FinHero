package com.finhero.finhero.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    
    @NotNull(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;
    
    @NotNull(message = "Senha é obrigatória")
    @Size(min = 1, message = "Senha é obrigatória")
    private String password;
}

