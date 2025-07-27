package com.edutech.api.domain.curso.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusCurso {
    ATIVO("Ativo"),
    INATIVO("Inativo");

    private final String descricao;

    StatusCurso(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusCurso fromJson(String value) {
        for (StatusCurso status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Status de curso invalida: " + value);
    }
}
