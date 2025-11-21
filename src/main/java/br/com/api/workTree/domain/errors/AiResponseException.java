package br.com.api.workTree.domain.errors;

public class AiResponseException extends RuntimeException {
    public AiResponseException(String message) {
        super(message);
    }
}
