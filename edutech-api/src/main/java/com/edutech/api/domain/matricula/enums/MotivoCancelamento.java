package com.edutech.api.domain.matricula.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MotivoCancelamento {
    DESISTENCIA("Desistencia"),
    INADIMPLENCIA("Inadimplencia"),
    TRANSFERENCIA("Transferencia"),
    OUTROS("Outros");

    private final String descricao;

    MotivoCancelamento(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static MotivoCancelamento fromJson(String value) {
        for (MotivoCancelamento motivoCancelamento : values()) {
            if (motivoCancelamento.name().equalsIgnoreCase(value)) {
                return motivoCancelamento;
            }
        }
        throw new IllegalArgumentException("Categoria de curso invalida: " + value);
    }
}
