package br.com.api.jobplatform.services;

import br.com.api.jobplatform.domain.dtos.JobRequestDTO;
import br.com.api.jobplatform.domain.dtos.JobResponseDTO;
import br.com.api.jobplatform.domain.entities.Job;
import br.com.api.jobplatform.domain.entities.User;
import br.com.api.jobplatform.domain.enums.UserRole;
import br.com.api.jobplatform.domain.errors.BusinessException;
import br.com.api.jobplatform.domain.errors.NotFoundException;
import br.com.api.jobplatform.repositories.JobRepository;
import br.com.api.jobplatform.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository repository;

    @Autowired
    private UserRepository userRepository;

    public List<JobResponseDTO> obterTodos(Pageable pageable) {
         return repository.findByActiveTrue(pageable).stream().map(JobResponseDTO::from).toList();
//        return repository.findAll()
//                .stream()
//                .map(JobResponseDTO::from)
//                .toList();
    }

    public JobResponseDTO obterPorId(Long id) {
        return repository.findById(id).map(JobResponseDTO::from)
                .orElseThrow(() -> new NotFoundException("Vaga de emprego não encontrada com ID: " + id));
    }

    public JobResponseDTO save(JobRequestDTO dto, Long createdByUserId) {
        Optional<User> byId = this.userRepository.findById(createdByUserId);
        if (byId.isEmpty()) throw new NotFoundException("Empresa não encontrada");

        User createdByUser = byId.get();
        if (createdByUser.getRole() != UserRole.COMPANY) throw new BusinessException("Ação não permitida para esse usuário");

        Job job = getJob(dto, createdByUser);

        Job save = repository.save(job);
        return JobResponseDTO.from(save);
    }

    public JobResponseDTO atualizar(Long id, JobRequestDTO dto) {
        Job jobToUpdate = repository.findById(id).orElseThrow(() -> new NotFoundException("Vaga não encontrado"));

        jobToUpdate.setTitle(dto.title());
        jobToUpdate.setLocation(dto.location());
        jobToUpdate.setCategory(dto.category());
        jobToUpdate.setType(dto.type());
        jobToUpdate.setSalary(dto.salary());
        jobToUpdate.setDescription(dto.description());
        jobToUpdate.setRequirements(dto.requirements());

        Job updatedJob = repository.save(jobToUpdate);
        return JobResponseDTO.from(updatedJob);
    }

    public void deletar(Long id) {
        Job jobToDelete = repository.findById(id).orElseThrow(() -> new NotFoundException("Vaga não encontrado"));

        jobToDelete.setActive(false);

        repository.save(jobToDelete);
    }

    private static Job getJob(JobRequestDTO dto, User createdByUser) {
        Job job = new Job();

        job.setTitle(dto.title());
        job.setCompany(createdByUser.getName());
        job.setLocation(dto.location());
        job.setCategory(dto.category());
        job.setType(dto.type());
        job.setSalary(dto.salary());
        job.setDescription(dto.description());
        job.setRequirements(dto.requirements());
        job.setCreatedBy(createdByUser);
        return job;
    }
}
