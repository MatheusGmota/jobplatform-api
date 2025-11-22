package br.com.api.jobplatform.domain.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail fornecido não é válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        String password
) {}
