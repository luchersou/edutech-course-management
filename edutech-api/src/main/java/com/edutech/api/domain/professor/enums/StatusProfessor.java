package com.edutech.api.domain.professor.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusProfessor {
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    AFASTADO("Afastado");

    private final String descricao;

    StatusProfessor(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusProfessor fromJson(String value) {
        for (StatusProfessor status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status do professor inválido: " + value);
    }
}

