package com.edutech.api.domain.curso.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NivelCurso {
    INTRODUCAO("Introdução"),
    BASICO("Básico"),
    INTERMEDIARIO("Intermediário"),
    AVANCADO("Avançado"),
    ESPECIALIZACAO("Especialização");

    private final String descricao;

    NivelCurso(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static NivelCurso fromJson(String value) {
        for (NivelCurso nivel : values()) {
            if (nivel.name().equalsIgnoreCase(value)) {
                return nivel;
            }
        }
        throw new IllegalArgumentException("Nível de curso inválido: " + value);
    }
}