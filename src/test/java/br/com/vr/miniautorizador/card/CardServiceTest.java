package br.com.vr.miniautorizador.card;

import br.com.vr.miniautorizador.card.exceptions.CardAlreadyExistsException;
import br.com.vr.miniautorizador.card.exceptions.CardNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    private CardEntity cardEntity;

    private BigDecimal initialBalance;

    @BeforeEach
    void setUp() {
        initialBalance = BigDecimal.valueOf(500.00);
        cardEntity = new CardEntity("1", "6549873025634501", "1234", initialBalance);
    }

    @Test
    void shouldCreateACreditCardWithInitialBalanceSuccessfully() {
        when(cardRepository.findByCardNumber(cardEntity.getCardNumber())).thenReturn(Optional.empty());
        when(cardRepository.save(any(CardEntity.class))).thenReturn(cardEntity);
        Optional<CardEntity> result = cardService.create(cardEntity);
        assertTrue(result.isPresent());
        assertEquals(initialBalance, result.get().getBalance());
        verify(cardRepository).save(cardEntity);
    }

    @Test
    void shouldCreateACreditCardWithAlreadyExistentNumberError() {
        when(cardRepository.findByCardNumber(cardEntity.getCardNumber())).thenReturn(Optional.of(cardEntity));
        assertThrows(CardAlreadyExistsException.class, () -> cardService.create(cardEntity));
        verify(cardRepository, never()).save(any(CardEntity.class));
    }

    @Test
    void shouldReceiveAExistentCardNumberAndReturnDataSuccessfully() {
        when(cardRepository.findByCardNumber(cardEntity.getCardNumber())).thenReturn(Optional.of(cardEntity));
        Optional<CardEntity> result = cardService.getByNumber(cardEntity.getCardNumber());
        assertTrue(result.isPresent());
        assertEquals(cardEntity, result.get());
    }

    @Test
    void shouldReceiveANonExistentCardNumberAndThrowAnError() {
        when(cardRepository.findByCardNumber(cardEntity.getCardNumber())).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getByNumber(cardEntity.getCardNumber()));
    }

    @Test
    void shouldFindAndGetBalanceFromAnExistentCardSuccessfully() {
        when(cardRepository.findByCardNumber(cardEntity.getCardNumber())).thenReturn(Optional.of(cardEntity));
        BigDecimal result = cardService.getBalance(cardEntity.getCardNumber());
        assertEquals(cardEntity.getBalance(), result);
    }

    @Test
    void shouldTryToFindANonExistentCardError() {
        when(cardRepository.findByCardNumber(cardEntity.getCardNumber())).thenReturn(Optional.empty());
        assertThrows(CardNotFoundException.class, () -> cardService.getBalance(cardEntity.getCardNumber()));
    }

    @Test
    void shouldUpdateBalanceSuccess() {
        BigDecimal amount = BigDecimal.valueOf(100);
        cardEntity.setBalance(cardEntity.getBalance().add(amount));
        when(cardRepository.save(any(CardEntity.class))).thenReturn(cardEntity);
        Optional<CardEntity> result = cardService.updateBalance(cardEntity, amount);
        assertTrue(result.isPresent());
        assertEquals(cardEntity.getBalance(), result.get().getBalance());
        verify(cardRepository).save(cardEntity);
    }
}