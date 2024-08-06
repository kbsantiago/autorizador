package br.com.vr.miniautorizador.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record Transaction (
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
    @JsonProperty("valor")
    @Min(value = 1, message = "Deve ser maior que 0.")
    BigDecimal amount
){}
