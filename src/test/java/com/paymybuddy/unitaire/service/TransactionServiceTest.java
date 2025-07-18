package com.paymybuddy.unitaire.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User userSource;
    private User userTarget;

    @BeforeEach
    void setUp() {
        userSource = new User();
        userSource.setUserId(1L);
        userSource.setSolde(new BigDecimal("1000.00"));

        userTarget = new User();
        userTarget.setUserId(2L);
        userTarget.setSolde(new BigDecimal("500.00"));
    }

    /**
     * Vérifie que les transactions sont paginées correctement.
     */
    @Test
    void displayTransaction_shouldReturnPaginatedTransactions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Transaction transaction = new Transaction();
        Page<Transaction> expectedPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.listTransactions(1L, pageable)).thenReturn(expectedPage);

        // Act
        Page<Transaction> result = transactionService.displayTransaction(1L, 0, 10);

        // Assert
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).listTransactions(1L, pageable);
    }

    /**
     * Vérifie que le transfert entre deux utilisateurs fonctionne correctement.
     */
    @Test
    void transfertAmount_shouldTransferCorrectly() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(userSource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userTarget));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        transactionService.transfertAmount(1L, 2L, "Paiement", 100.00);

        // Assert
        assertEquals(new BigDecimal("899.50"), userSource.getSolde()); // 100 + 0.50 de frais
        assertEquals(new BigDecimal("600.00"), userTarget.getSolde());
    }

    /**
     * Vérifie qu'une exception est levée si le montant est nul ou négatif.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfertAmount(1L, 2L, "Test", 0));
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfertAmount(1L, 2L, "Test", -50));
    }

    /**
     * Vérifie qu'une exception est levée si l'utilisateur source est introuvable.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenSourceUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.transfertAmount(1L, 2L, "Test", 50));
        assertTrue(exception.getMessage().contains("Expéditeur non trouvé"));
    }

    /**
     * Vérifie qu'une exception est levée si l'utilisateur cible est introuvable.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenTargetUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userSource));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.transfertAmount(1L, 2L, "Test", 50));
        assertTrue(exception.getMessage().contains("Destinataire non trouvé"));
    }

    /**
     * Vérifie que le service lève une exception en cas de solde insuffisant.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenSoldeIsInsufficient() {
        userSource.setSolde(new BigDecimal("50.00")); // 100.00 + 0.50 = trop

        when(userRepository.findById(1L)).thenReturn(Optional.of(userSource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userTarget));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfertAmount(1L, 2L, "Test", 100));
        assertTrue(exception.getMessage().contains("Solde insuffisant"));
    }

    /**
     * Vérifie que la méthode récupère correctement les transactions de l'utilisateur.
     */
    @Test
    void getTransactionsForUser_shouldReturnTransactionList() {
        List<Transaction> mockList = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.findByUserSenderUserIdOrUserReceiverUserId(1L, 1L)).thenReturn(mockList);

        List<Transaction> result = transactionService.getTransactionsForUser(1L);

        assertEquals(2, result.size());
        verify(transactionRepository).findByUserSenderUserIdOrUserReceiverUserId(1L, 1L);
    }
}
