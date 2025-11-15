package br.com.api.workTree.domain.entities;

import br.com.api.workTree.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity(name = "TB_APPLICATION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "application", sequenceName = "SQ_TB_APPLICATION", allocationSize = 1)
public class Application {
    @Id
    @GeneratedValue(generator = "application", strategy = GenerationType.SEQUENCE)
    @Column(name = "id_application", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime appliedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime reviewedAt;

    private String coverLetter;
}
