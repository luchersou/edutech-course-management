package com.edutech.api.domain.curso.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoriaCurso {
    PROGRAMACAO("Programação"),
    BANCO_DADOS("Banco de Dados"),
    DESIGN_GRAFICO("Design Grafico"),
    REDES("Redes"),
    SEGURANCA("Segurança da Informação"),
    MOBILE("Desenvolvimento Mobile"),
    WEB("Desenvolvimento Web"),
    CLOUD("Cloud Computing");

    private final String descricao;

    CategoriaCurso(String descricao) {
        this.descricao = descricao;
    }

    @JsonValue
    public String getDescricao() {
        return descricao;
    }

    @JsonCreator
    public static CategoriaCurso fromJson(String value) {
        for (CategoriaCurso categoria : values()) {
            if (categoria.name().equalsIgnoreCase(value)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria de curso invalida: " + value);
    }
}