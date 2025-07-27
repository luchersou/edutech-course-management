package com.edutech.api.domain.matricula.dto;

import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import jakarta.validation.constraints.NotNull;

public record CancelarMatriculaDTO(
        @NotNull(message = "Motivo do cancelamento é obrigatório")
        MotivoCancelamento motivo
) {}
