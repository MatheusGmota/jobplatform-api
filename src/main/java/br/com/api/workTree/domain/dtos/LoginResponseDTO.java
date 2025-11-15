package br.com.api.workTree.domain.dtos;

import br.com.api.workTree.domain.entities.User;

public record LoginResponseDTO(Long id, String username, String token) {
}
