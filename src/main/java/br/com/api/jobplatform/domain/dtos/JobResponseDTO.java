package br.com.api.jobplatform.domain.dtos;

import br.com.api.jobplatform.domain.entities.Job;
import br.com.api.jobplatform.domain.enums.JobType;

import java.time.LocalDateTime;
import java.util.List;

public record JobResponseDTO(
        Long id,
        String title,
        String company,
        String location,
        String category,
        JobType type,
        String salary,
        String description,
        List<String> requirements,
        Long createdByUserId,
        LocalDateTime postedAt,
        LocalDateTime updatedAt,
        Boolean active
) {
    public static JobResponseDTO from(Job job) {
        Long createdByUserId = (job.getCreatedBy() != null) ? job.getCreatedBy().getId() : null;

        return new JobResponseDTO(
                job.getId(),
                job.getTitle(),
                job.getCompany(),
                job.getLocation(),
                job.getCategory(),
                job.getType(),
                job.getSalary(),
                job.getDescription(),
                job.getRequirements(),
                createdByUserId,
                job.getPostedAt(),
                job.getUpdatedAt(),
                job.getActive()
        );
    }
}
