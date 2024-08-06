package br.com.vr.miniautorizador.card;

import br.com.vr.miniautorizador.card.exceptions.CardAlreadyExistsException;
import br.com.vr.miniautorizador.card.exceptions.CardNotFoundException;
import br.com.vr.miniautorizador.common.constants.MsgProperties;
import br.com.vr.miniautorizador.common.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {
    private final BigDecimal INITIAL_BALANCE = BigDecimal.valueOf(500.00);
    private final CardRepository cardRepository;

    public Optional<CardEntity> create(final CardEntity cardEntity) {
        cardRepository.findByCardNumber(cardEntity.getCardNumber())
                .ifPresent(card -> {
                    throw new CardAlreadyExistsException(
                            JsonUtil.toJson(CardMapper.INSTANCE.convertWithoutBalance(card))
                    );
                });

        cardEntity.setBalance(INITIAL_BALANCE);
        return Optional.of(cardRepository.save(cardEntity));
    }

    public Optional<CardEntity> getByNumber(final String cardNumber) {
        return Optional.of(cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> {
            throw new CardNotFoundException(MsgProperties.CARD_DOES_NOT_EXISTS);
        }));
    }

    public BigDecimal getBalance(final String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber).orElseThrow(() -> {
            throw new CardNotFoundException(MsgProperties.CARD_DOES_NOT_EXISTS);
        }).getBalance();
    }

    public Optional<CardEntity> updateBalance(final CardEntity cardEntity, final BigDecimal amount) {
        cardEntity.setBalance(cardEntity.getBalance().subtract(amount));
        return Optional.of(cardRepository.save(cardEntity));
    }
}
