package br.com.api.workTree.services;

import br.com.api.workTree.domain.entities.Application;
import br.com.api.workTree.domain.entities.Job;
import br.com.api.workTree.domain.entities.User;
import br.com.api.workTree.domain.errors.AiResponseException;
import br.com.api.workTree.infra.config.DeepseekConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DeepseekService {

    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DeepseekConfig config;

    public String generateApplicationConfirmation(User candidate, Job job, Application application) {
        String prompt = """
                Gere um e-mail curto, profissional e amigável confirmando a candidatura.
                Dados:
                - Nome do candidato: %s
                - Vaga: %s
                - Empresa: %s
                - Data da candidatura: %s

                O texto deve ser formal, agradecendo a candidatura e indicando que será analisada.
                Assine como "Equipe de Recrutamento".
                """.formatted(
                candidate.getName(),
                job.getTitle(),
                job.getCompany(),
                application.getAppliedAt()
        );

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");

            Map<String, String> systemMessage = Map.of(
                    "role", "system",
                    "content", "You are a helpful assistant."
            );

            Map<String, String> userMsg = Map.of(
                    "role", "user",
                    "content", prompt
            );

            requestBody.put("messages", List.of(systemMessage, userMsg));
            requestBody.put("stream", false);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(config.getApiKey());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getBody());

                // Validação segura
                if (root.has("choices") && root.path("choices").isArray() && root.path("choices").size() > 0) {
                    JsonNode content = root.path("choices").get(0).path("message").path("content");
                    if (!content.isMissingNode()) {
                        return content.asText();
                    }
                }
                throw new AiResponseException("Resposta inesperada da API Deepseek.");
            } else {
                throw new AiResponseException("Erro da API Deepseek: " + response.getStatusCode() + " - " + response.getBody());
            }

        } catch (Exception e) {
            throw new AiResponseException("Erro ao conectar ao Deepseek: " + e.getMessage());
        }
    }
}
