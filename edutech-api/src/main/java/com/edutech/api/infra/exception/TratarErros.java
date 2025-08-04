package com.edutech.api.infra.exception;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.infra.dto.DadosErroResposta;
import com.edutech.api.infra.dto.DadosErroValidacao;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class TratarErros {

    private static final Logger logger = LoggerFactory.getLogger(TratarErros.class);

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosErroResposta> tratarErroRegraDeNegocio(ValidacaoException ex) {
        return ResponseEntity.badRequest()
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DadosErroResposta> tratarErro404() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.NOT_FOUND.value(),
                        "Recurso não encontrado"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DadosErroResposta> tratarErro400(MethodArgumentNotValidException ex) {
        List<DadosErroValidacao> erros = ex.getFieldErrors().stream()
                .map(DadosErroValidacao::new)
                .toList();
        return ResponseEntity.badRequest()
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro de validação",
                        erros
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErroResposta> tratarErro400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro na leitura do corpo da requisição. Verifique o JSON enviado."
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<DadosErroResposta> tratarMetodoNaoPermitido(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "Método HTTP não permitido para este recurso. Métodos suportados: " + ex.getSupportedHttpMethods()
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DadosErroResposta> tratarErroBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.UNAUTHORIZED.value(),
                        "Credenciais inválidas"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DadosErroResposta> tratarErro500(Exception ex) {
        logger.error("Erro interno no servidor", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DadosErroResposta(
                        LocalDateTime.now(),
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Erro interno no servidor: " + ex.getLocalizedMessage()
                ));
    }

}
