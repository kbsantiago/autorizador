package br.com.vr.miniautorizador.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record Transaction (
    @JsonProperty("numeroCartao")
    String cardNumber,
    @JsonProperty("senha")
    String password,
    @JsonProperty("valor")
    BigDecimal amount
){}
