package br.com.api.jobplatform.domain.errors;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
