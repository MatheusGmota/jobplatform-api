package br.com.api.jobplatform.domain.dtos;

import br.com.api.jobplatform.domain.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RegisterRequestDTO(
        @NotBlank(message = "O nome é obrigatório.")
        @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
        String name,

        @NotBlank(message = "O e-mail é obrigatório.")
        @Email(message = "O e-mail fornecido não é válido.")
        String email,

        @NotBlank(message = "A senha é obrigatória.")
        @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
        String password,

        @NotNull(message = "O tipo de usuário (CANDIDATE/COMPANY) é obrigatório.")
        UserRole type,

        List<String> skills,

        @Size(max = 500, message = "A descrição não pode exceder 500 caracteres.")
        String description
) {}
