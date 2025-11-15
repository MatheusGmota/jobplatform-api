package br.com.api.workTree.domain.dtos;

import br.com.api.workTree.domain.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public record RegisterResponseDTO(
        Long userId,
        String name,
        String email,
        List<String> skills,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static RegisterResponseDTO from(User user) {
        return new RegisterResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getSkills(),
                user.getDescription(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
