package br.com.api.jobplatform.domain.dtos;

import br.com.api.jobplatform.domain.entities.Application;
import br.com.api.jobplatform.domain.enums.ApplicationStatus;

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
