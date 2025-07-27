package com.edutech.api.domain.turma.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum StatusTurma {
    ABERTA("Aberta"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada");

    private final String descricao;

    StatusTurma(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static StatusTurma fromJson(String value) {
        for (StatusTurma statusTurma : values()) {
            if (statusTurma.name().equalsIgnoreCase(value)) {
                return statusTurma;
            }
        }
        throw new IllegalArgumentException("Modalidade de curso inválida: " + value);
    }
}
