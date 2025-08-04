package com.edutech.api.infra.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public record DadosErroResposta(
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime timestamp,
        int status,
        String mensagem,
        List<DadosErroValidacao> erros
) {
    public DadosErroResposta(LocalDateTime timestamp, int status, String mensagem) {
        this(timestamp, status, mensagem, null);
    }
}
