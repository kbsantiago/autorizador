package br.com.vr.miniautorizador.transaction;

import br.com.vr.miniautorizador.card.CardEntity;
import br.com.vr.miniautorizador.card.CardService;
import br.com.vr.miniautorizador.transaction.exceptions.InsufficientBalanceException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidCardException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CardService cardService;
    private CardEntity cardEntity;

    public void executeTransaction(final Transaction transaction) {
        cardEntity = getCardByNumber(transaction.cardNumber());
        synchronized (cardEntity) {
            validateProvidedPassword(transaction.password());
            validateSufficientBalance(transaction.amount());
            cardService.updateBalance(cardEntity, transaction.amount());
        }
    }

    private CardEntity getCardByNumber(final String cardNumber) {
        return cardService.getByNumber(cardNumber)
                .orElseThrow(() -> {
                    throw new InvalidCardException(TransactionStatus.NON_EXISTENT_CARD.getMessage());
                });
    }

    private void validateProvidedPassword(final String providedPassword) {
       if(!cardEntity.getPassword().equals(providedPassword)) {
           throw new InvalidPasswordException(TransactionStatus.INVALID_PASSWORD.getMessage());
       }
    }

    private void validateSufficientBalance(final BigDecimal amount) {
        if(amount.compareTo(cardEntity.getBalance()) > 0) {
            throw new InsufficientBalanceException(TransactionStatus.INSUFFICIENT_BALANCE.getMessage());
        }
    }
}
