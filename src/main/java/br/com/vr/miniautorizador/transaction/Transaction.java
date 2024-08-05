package br.com.vr.miniautorizador.transaction;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record Transaction (
    @NotNull
    @NotEmpty(message = "Card number is required.")
    @NotBlank(message = "Card number is required.")
    @Pattern(regexp = "^[0-9]{13,19}$", message="Invalid card number. Must be a sequence of 13 to 19 digits.")
    String cardNumber,
    @NotNull
    @NotEmpty(message = "Password is required.")
    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^[0-9]{4}$", message="Invalid input. Must be exactly 4 digits.")
    String password,
    @NotNull
    @NotEmpty(message = "Amount is required.")
    @NotBlank(message = "Amount is required.")
    @Min(value = 0, message = "Amount must be greater than zero.")
    BigDecimal amount
){}
