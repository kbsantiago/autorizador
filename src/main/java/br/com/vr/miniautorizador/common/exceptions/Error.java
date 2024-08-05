package br.com.vr.miniautorizador.common.exceptions;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Error {
    private final LocalDateTime executionDateAndTime;
    private final int statusCode;
    private final String errorMessage;

    public Error(int statusCode, String errorMessage) {
        this.executionDateAndTime = LocalDateTime.now();
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
}
