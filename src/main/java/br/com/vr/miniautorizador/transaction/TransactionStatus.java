package br.com.vr.miniautorizador.transaction;

public enum TransactionStatus {
    INSUFFICIENT_BALANCE ("Saldo insuficiente"),
    INVALID_PASSWORD ("Senha inválida"),
    NON_EXISTENT_CARD ("Cartão inexistente");

    private final String message;

    TransactionStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
