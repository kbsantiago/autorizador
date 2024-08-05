package br.com.vr.miniautorizador.common.openapi;

import br.com.vr.miniautorizador.card.Card;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cartoes")
@Tag(name = "cartões", description = "API de cartões")
public interface CardApi {

    @Operation(description = "Cria um cartão")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Card> createCard(@Valid @RequestBody final Card card);

    @Operation(description = "Obtém o saldo de um cartão")
    @GetMapping(value = "/{numeroCartao}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Card> getBalance(@PathVariable("numeroCartao") final String numeroCartao);
}
