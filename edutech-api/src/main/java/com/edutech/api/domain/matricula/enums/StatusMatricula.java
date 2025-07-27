package com.edutech.api.domain.matricula.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusMatricula {
    ATIVA("Ativa"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    TRANCADA("Trancada");

    private final String descricao;

    StatusMatricula(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusMatricula fromJson(String value) {
        for (StatusMatricula statusMatricula : values()) {
            if (statusMatricula.name().equalsIgnoreCase(value)) {
                return statusMatricula;
            }
        }
        throw new IllegalArgumentException("Categoria de curso invalida: " + value);
    }
}
