package br.com.vr.miniautorizador.transaction.exceptions;

public class InvalidCardException extends RuntimeException{
    public InvalidCardException(String message) {
        super(message);
    }
}
