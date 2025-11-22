package br.com.api.jobplatform.domain.dtos;

import br.com.api.jobplatform.domain.entities.User;
import br.com.api.jobplatform.domain.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public record UserResponseDTO(
        Long id,
        String name,
        String email,
        UserRole type,
        List<String> skills,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getSkills(),
                user.getDescription(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
