package br.com.api.workTree.domain.entities;

import br.com.api.workTree.domain.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "TB_JOBS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SequenceGenerator(name = "job", sequenceName = "SQ_TB_JOBS", allocationSize = 1)
public class Job {

    @Id
    @GeneratedValue(generator = "job", strategy = GenerationType.SEQUENCE)
    @Column(name = "job_id", nullable = false)
    private Long id;

    @Column(name = "job_title", nullable = false)
    private String title;

    @Column(nullable = false)
    private String company;

    @Column(name = "job_location",nullable = false)
    private String location;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(name = "job_type", nullable = false)
    private JobType type;

    private String salary;

    @Column(name = "job_description", nullable = false)
    private String description;

    @ElementCollection
    @CollectionTable(name = "job_requirements", joinColumns = @JoinColumn(name = "job_id"))
    @Column(name = "requirement")
    private List<String> requirements = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy; // empresa que cadastrou a vaga

    @CreationTimestamp
    private LocalDateTime postedAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Boolean active = true;
}
