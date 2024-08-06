package br.com.vr.miniautorizador.common.exceptions;

import br.com.vr.miniautorizador.card.exceptions.CardAlreadyExistsException;
import br.com.vr.miniautorizador.card.exceptions.CardNotFoundException;
import br.com.vr.miniautorizador.transaction.exceptions.InsufficientBalanceException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidCardException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidPasswordException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final int INTERNAL_SERVER_ERROR_STATUS_CODE = 500;
    private static final int BAD_REQUEST_STATUS_CODE = 400;

    private static final String MANDATORY_PARAM_ERROR_MESSAGE = "O parâmetro \"%s\" é obrigatório.";
    private static final String MANDATORY_HEADER_ERROR_MESSAGE = "O header \"%s\" é obrigatório.";

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<Error> globalExceptionHandler(final RuntimeException exception) {
        return ResponseEntity.internalServerError().body(
                new Error(INTERNAL_SERVER_ERROR_STATUS_CODE, exception.getMessage())
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<Error> missingServletRequestParameterExceptionHandler(
            final MissingServletRequestParameterException exception
    ) {
        return ResponseEntity.badRequest().body(
                new Error(
                        BAD_REQUEST_STATUS_CODE,
                        String.format(MANDATORY_PARAM_ERROR_MESSAGE, exception.getParameterName())
                )
        );
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    ResponseEntity<Error> missingRequestHeaderExceptionHandler(final MissingRequestHeaderException exception) {
        return ResponseEntity.badRequest().body(
                new Error(
                        BAD_REQUEST_STATUS_CODE,
                        String.format(MANDATORY_HEADER_ERROR_MESSAGE, exception.getHeaderName())
                )
        );
    }

    @ExceptionHandler(HttpClientErrorException.class)
    ResponseEntity<Error> httpClientErrorException(final HttpClientErrorException httpClientErrorException) {
        HttpStatusCode errorCode = httpClientErrorException.getStatusCode();
        return ResponseEntity
                .status(errorCode)
                .body(new Error(errorCode.value(),httpClientErrorException.getMessage())
                );
    }

    @ExceptionHandler(InvalidCardException.class)
    ResponseEntity<String> invalidCardException(final InvalidCardException invalidCardException) {
        return ResponseEntity.unprocessableEntity()
                .body(invalidCardException.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    ResponseEntity<String> invalidPasswordException(final InvalidPasswordException invalidPasswordException) {
        return ResponseEntity.unprocessableEntity()
                .body(invalidPasswordException.getMessage());
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    ResponseEntity<String> insufficientBalanceException(final InsufficientBalanceException insufficientBalanceException) {
        return ResponseEntity.unprocessableEntity()
                .body(insufficientBalanceException.getMessage());
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    ResponseEntity<String> cardAlreadyExistsException(final CardAlreadyExistsException cardAlreadyExistsException) {
        return ResponseEntity.unprocessableEntity()
                .body(cardAlreadyExistsException.getMessage());
    }

    @ExceptionHandler(CardNotFoundException.class)
    ResponseEntity<Error> CardNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
