package br.com.vr.miniautorizador.card;

import br.com.vr.miniautorizador.common.openapi.CardApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static br.com.vr.miniautorizador.card.CardMapper.INSTANCE;

@RestController
@RequiredArgsConstructor
public class CardController implements CardApi {
    private final CardService cardService;

    @Override
    public ResponseEntity<Card> createCard(final Card card) {
        var newCard = INSTANCE.convert(cardService.create(INSTANCE.convert(card)).get());
        return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
    }

    @Override
    public ResponseEntity getBalance(final String cardNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(cardService.getBalance(cardNumber));
    }
}
