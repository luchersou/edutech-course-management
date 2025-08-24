package com.edutech.api.domain.endereco;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@Getter
public class Endereco {

    private String logradouro;
    private String bairro;
    private String cep;
    private String numero;
    private String complemento;
    private String cidade;
    private String uf;

    protected Endereco (){}

    public Endereco(String logradouro,
                    String bairro,
                    String cep,
                    String numero,
                    String complemento,
                    String cidade,
                    String uf) {
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.cep = cep;
        this.numero = numero;
        this.complemento = complemento;
        this.cidade = cidade;
        this.uf = uf;
    }
}