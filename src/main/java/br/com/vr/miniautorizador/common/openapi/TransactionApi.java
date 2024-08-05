package br.com.vr.miniautorizador.common.openapi;

import br.com.vr.miniautorizador.transaction.Transaction;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/transacoes")
@Tag(name = "transações", description = "API de transações")
public interface TransactionApi {

    @Operation(description = "Realiza uma transação")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Transaction> createTransaction(@Valid @RequestBody final Transaction transaction);
}
