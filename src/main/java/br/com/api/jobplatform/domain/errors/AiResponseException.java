package br.com.api.jobplatform.domain.errors;

public class AiResponseException extends RuntimeException {
    public AiResponseException(String message) {
        super(message);
    }
}
