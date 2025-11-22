package br.com.api.jobplatform.domain.enums;

import lombok.Getter;

@Getter
public enum UserRole {

    COMPANY("company"),

    CANDIDATE("candidate");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

}
