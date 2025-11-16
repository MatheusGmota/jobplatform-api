package br.com.api.workTree.domain.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApplicationRequestDTO(
        @NotNull(message = "O ID da vaga é obrigatório.")
        Long jobId,

        @NotNull(message = "O ID do candidato é obrigatório.")
        Long candidateId,

        @NotBlank(message = "O currículo é obrigatório.")
        @Size(min = 2, max = 500, message = "A localização deve ter entre 2 e 500 caracteres.")
        String coverLetter
) {
}
