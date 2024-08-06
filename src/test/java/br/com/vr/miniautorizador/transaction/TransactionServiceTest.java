package br.com.vr.miniautorizador.transaction;

import br.com.vr.miniautorizador.card.CardEntity;
import br.com.vr.miniautorizador.card.CardService;
import br.com.vr.miniautorizador.card.exceptions.CardNotFoundException;
import br.com.vr.miniautorizador.transaction.exceptions.InsufficientBalanceException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidCardException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private TransactionService transactionService;

    private CardEntity cardEntity;

    private BigDecimal initialBalance;

    @BeforeEach
    public void setUp() {
        initialBalance = BigDecimal.valueOf(500.00);
        cardEntity = new CardEntity("1", "6549873025634501", "1234", initialBalance);
    }

    @Test
    public void shouldExecuteTransactionSuccessfully() {
        Transaction transaction = new Transaction("1234567890123456", "1234", BigDecimal.valueOf(50.0));
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));
        transactionService.executeTransaction(transaction);
        verify(cardService, times(1)).updateBalance(cardEntity, BigDecimal.valueOf(50.0));
    }

    @Test
    public void shouldExecuteTransactionWithInvalidCardNumberError() {
        Transaction transaction = new Transaction("0000000000000000", "1234", BigDecimal.valueOf(50.0));
        when(cardService.getByNumber("0000000000000000")).thenReturn(Optional.empty());
        doThrow(new CardNotFoundException("Card not found")).when(cardService).getByNumber("0000000000000000");
        assertThrows(InvalidCardException.class, () -> transactionService.executeTransaction(transaction));
    }

    @Test
    public void shouldExecuteTransactionWithInvalidPasswordError() {
        Transaction transaction = new Transaction("1234567890123456", "0000", BigDecimal.valueOf(50.0));
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));
        assertThrows(InvalidPasswordException.class, () -> transactionService.executeTransaction(transaction));
    }

    @Test
    public void shouldExecuteTransactionWithInsufficientBalanceError() {
        Transaction transaction = new Transaction("1234567890123456", "1234", BigDecimal.valueOf(2000.0));
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));
        assertThrows(InsufficientBalanceException.class, () -> transactionService.executeTransaction(transaction));
    }

    @Test
    public void shouldExecuteTransactionRaceCondition() throws InterruptedException {
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));

        BigDecimal transactionAmount = BigDecimal.valueOf(10.0);
        int numberOfThreads = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    Transaction transaction = new Transaction("1234567890123456", "1234", transactionAmount);
                    transactionService.executeTransaction(transaction);
                } catch (InsufficientBalanceException e) {
                    throw new InsufficientBalanceException(TransactionStatus.SALDO_INSUFICIENTE.name());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        verify(cardService, atMost(numberOfThreads)).updateBalance(any(CardEntity.class), eq(transactionAmount));
    }
}