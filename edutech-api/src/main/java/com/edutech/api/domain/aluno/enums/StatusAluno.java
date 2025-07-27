package com.edutech.api.domain.aluno.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusAluno {
    ATIVO("Ativo"),
    INATIVO("Inativo"),
    FORMADO("Formado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusAluno(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusAluno fromJson(String value) {
        for (StatusAluno status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status do aluno inválido: " + value);
    }
}
