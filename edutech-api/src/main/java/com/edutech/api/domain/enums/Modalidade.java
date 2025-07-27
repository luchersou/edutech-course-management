package com.edutech.api.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Modalidade {
    PRESENCIAL("Presencial"),
    EAD("EAD"),
    HIBRIDO("Hibrido");

    private final String descricao;

    Modalidade(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static Modalidade fromJson(String value) {
        for (Modalidade modalidade : values()) {
            if (modalidade.name().equalsIgnoreCase(value)) {
                return modalidade;
            }
        }
        throw new IllegalArgumentException("Modalidade de curso invalida: " + value);
    }
}
