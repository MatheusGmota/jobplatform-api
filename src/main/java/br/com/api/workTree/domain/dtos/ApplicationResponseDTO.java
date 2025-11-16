package br.com.api.workTree.domain.dtos;

import br.com.api.workTree.domain.entities.Application;
import br.com.api.workTree.domain.enums.ApplicationStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

public record ApplicationResponseDTO(
        Long id,
        String coverLetter,
        Long jobId,
        Long candidateId,
        ApplicationStatus status,
        LocalDateTime appliedAt,
        LocalDateTime updatedAt,
        LocalDateTime reviewedAt
){
    public static ApplicationResponseDTO from(Application app) {
        return new ApplicationResponseDTO(
                app.getId(),
                app.getCoverLetter(),
                app.getJob().getId(),
                app.getCandidate().getId(),
                app.getStatus(),
                app.getAppliedAt(),
                app.getUpdatedAt(),
                app.getReviewedAt());
    }
}
