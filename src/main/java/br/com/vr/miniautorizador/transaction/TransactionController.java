package br.com.vr.miniautorizador.transaction;

import br.com.vr.miniautorizador.common.openapi.TransactionApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionApi {
    private final TransactionService transactionService;

    @Override
    public ResponseEntity<Transaction> createTransaction(final Transaction transaction) {
        transactionService.executeTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
