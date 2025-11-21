package br.com.api.workTree.services;

import br.com.api.workTree.domain.dtos.ApplicationRequestDTO;
import br.com.api.workTree.domain.dtos.ApplicationResponseDTO;
import br.com.api.workTree.domain.entities.Application;
import br.com.api.workTree.domain.entities.Job;
import br.com.api.workTree.domain.entities.User;
import br.com.api.workTree.domain.enums.ApplicationStatus;
import br.com.api.workTree.domain.errors.AiResponseException;
import br.com.api.workTree.domain.errors.BusinessException;
import br.com.api.workTree.domain.errors.NotFoundException;
import br.com.api.workTree.domain.model.EmailModel;
import br.com.api.workTree.repositories.ApplicationRepository;
import br.com.api.workTree.repositories.JobRepository;
import br.com.api.workTree.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationService {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private DeepseekService deepseekService;

    @Autowired
    private ApplicationRepository repository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    public ApplicationResponseDTO apply(ApplicationRequestDTO dto) {
        if (repository.existsByJobIdAndCandidateId(dto.jobId(), dto.candidateId())) {
            throw new BusinessException("Já existe candidatura para essa vaga por este candidato");
        }

        Job job = jobRepository.findById(dto.jobId())
                .orElseThrow(() -> new NotFoundException("Vaga não encontrada"));

        // verificar se vaga está ativa
        if (!job.getActive()) throw new BusinessException("A vaga não está mais disponível");

        User candidate = userRepository.findById(dto.candidateId())
                .orElseThrow(() -> new NotFoundException("Candidato não encontrado"));

        Application app = new Application();
        app.setJob(job);
        app.setCandidate(candidate);
        app.setCoverLetter(dto.coverLetter());
        app.setStatus(ApplicationStatus.PENDING);

        Application saved = repository.save(app);

        String text = "Confirmação de inscrição na vaga de" + job.getTitle() + ". Fique atento as atualização pelo email";
        try {
//            String text = aiService.generateApplicationConfirmation(candidate, job, app);
            text = deepseekService.generateApplicationConfirmation(candidate, job, app);

        } catch (AiResponseException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            EmailModel emailModel = new EmailModel(
                    candidate.getEmail(),
                    "Confirmação de candidatura para a vaga: " + job.getTitle(),
                    text,
                    null);

            emailSenderService.sendEmail(emailModel);
        }

        return ApplicationResponseDTO.from(saved);
    }

    public ApplicationResponseDTO obterPorId(Long id) {
        return repository.findById(id).map(ApplicationResponseDTO::from)
                .orElseThrow(() -> new NotFoundException("Nenhuma candidatura encontrada"));
    }
}
