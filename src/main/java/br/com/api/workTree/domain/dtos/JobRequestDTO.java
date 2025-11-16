package br.com.api.workTree.domain.dtos;

import br.com.api.workTree.domain.enums.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.util.List;

public record JobRequestDTO(
        @NotBlank(message = "O título da vaga é obrigatório.")
        @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres.")
        String title,

        @NotBlank(message = "O nome da empresa é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome da empresa deve ter entre 2 e 100 caracteres.")
        String company,

        @NotBlank(message = "A localização é obrigatória.")
        @Size(min = 2, max = 100, message = "A localização deve ter entre 2 e 100 caracteres.")
        String location,

        @NotBlank(message = "A categoria é obrigatória.")
        @Size(min = 2, max = 50, message = "A categoria deve ter entre 2 e 50 caracteres.")
        String category,

        @NotNull(message = "O tipo de vaga é obrigatório.")
        JobType type,

        @Size(max = 50, message = "O campo salário não pode exceder 50 caracteres.")
        String salary,

        @NotBlank(message = "A descrição da vaga é obrigatória.")
        @Size(min = 50, message = "A descrição deve ter pelo menos 50 caracteres.")
        String description,

        List<
                @NotBlank(message = "O requisito não pode ser vazio.")
                @Size(max = 255, message = "Cada requisito não pode exceder 255 caracteres.")
                        String> requirements
) {}
