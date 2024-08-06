package br.com.vr.miniautorizador.card;

import com.fasterxml.jackson.annotation.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Card (
    @JsonProperty("numeroCartao")
    @NotNull
    @NotEmpty(message = "Número do cartão é obrigatório.")
    @NotBlank(message = "Número do cartão é obrigatório.")
    @Pattern(regexp = "^[0-9]{13,19}$", message="Número do cartão é inválido. Deve ter uma sequência entre 13 a 19 dígitos.")
    String cardNumber,
    @JsonProperty("senha")
    @NotNull
    @NotEmpty(message = "Senha é obrigatória.")
    @NotBlank(message = "Senha é obrigatória.")
    @Pattern(regexp = "^[0-9]{4}$", message="Senha inválida.Deve ter exatamente 4 dígitos.")
    String password,
    @Null
    @JsonProperty("saldo")
    BigDecimal balance
){}
