package br.com.vr.miniautorizador.card;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Card (
    @JsonProperty("numeroCartao")
    String cardNumber,
    @JsonProperty("senha")
    String password,
    @JsonProperty("saldo")
    BigDecimal balance
){}
