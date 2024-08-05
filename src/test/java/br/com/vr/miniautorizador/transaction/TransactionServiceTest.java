package br.com.vr.miniautorizador.transaction;

import br.com.vr.miniautorizador.card.CardEntity;
import br.com.vr.miniautorizador.card.CardService;
import br.com.vr.miniautorizador.common.constants.MsgProperties;
import br.com.vr.miniautorizador.transaction.exceptions.InsufficientBalanceException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidCardException;
import br.com.vr.miniautorizador.transaction.exceptions.InvalidPasswordException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${application.params.initialBalance}")
    private BigDecimal initialBalance;

    @BeforeEach
    public void setUp() {
        initialBalance = BigDecimal.valueOf(500);
        cardEntity = new CardEntity("1", "6549873025634501", "1234", initialBalance);
    }

    @Test
    public void testExecuteTransaction_success() {
        Transaction transaction = new Transaction("1234567890123456", "1234", BigDecimal.valueOf(50.0));
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));
        transactionService.executeTransaction(transaction);
        verify(cardService, times(1)).updateBalance(cardEntity, BigDecimal.valueOf(50.0));
    }

    @Test
    public void testExecuteTransaction_invalidCard() {
        Transaction transaction = new Transaction("0000000000000000", "1234", BigDecimal.valueOf(50.0));
        when(cardService.getByNumber("0000000000000000")).thenReturn(Optional.empty());
        assertThrows(InvalidCardException.class, () -> transactionService.executeTransaction(transaction));
    }

    @Test
    public void testExecuteTransaction_invalidPassword() {
        Transaction transaction = new Transaction("1234567890123456", "0000", BigDecimal.valueOf(50.0));
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));
        assertThrows(InvalidPasswordException.class, () -> transactionService.executeTransaction(transaction));
    }

    @Test
    public void testExecuteTransaction_insufficientBalance() {
        Transaction transaction = new Transaction("1234567890123456", "1234", BigDecimal.valueOf(2000.0));
        when(cardService.getByNumber("1234567890123456")).thenReturn(Optional.of(cardEntity));
        assertThrows(InsufficientBalanceException.class, () -> transactionService.executeTransaction(transaction));
    }

    @Test
    public void testExecuteTransaction_raceCondition() throws InterruptedException {
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
                    throw new InsufficientBalanceException(TransactionStatus.INSUFFICIENT_BALANCE.getMessage());
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