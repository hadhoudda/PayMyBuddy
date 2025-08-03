package com.paymybuddy.unitaire.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class TransactionServiceTest {

    @MockitoBean
    private TransactionRepository transactionRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    private User userSource;
    private User userTarget;

    @BeforeEach
    void setUp() {
        // Arrange - création des utilisateurs source et cible avec soldes initialisés
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
        assertEquals(new BigDecimal("899.50"), userSource.getSolde()); // 100 + 0.50 de frais déduits
        assertEquals(new BigDecimal("600.00"), userTarget.getSolde());
    }

    /**
     * Vérifie qu'une exception est levée si le montant est nul ou négatif.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenAmountIsZeroOrNegative() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfertAmount(1L, 2L, "Test", 0));
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfertAmount(1L, 2L, "Test", -50));
    }

    /**
     * Vérifie qu'une exception est levée si l'utilisateur source est introuvable.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenSourceUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.transfertAmount(1L, 2L, "Test", 50));
        assertTrue(exception.getMessage().contains("Expéditeur non trouvé"));
    }

    /**
     * Vérifie qu'une exception est levée si l'utilisateur cible est introuvable.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenTargetUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(userSource));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.transfertAmount(1L, 2L, "Test", 50));
        assertTrue(exception.getMessage().contains("Destinataire non trouvé"));
    }

    /**
     * Vérifie que le service lève une exception en cas de solde insuffisant.
     */
    @Test
    void transfertAmount_shouldThrowExceptionWhenSoldeIsInsufficient() {
        // Arrange
        userSource.setSolde(new BigDecimal("50.00")); // Solde insuffisant pour couvrir 100 + 0.50 frais

        when(userRepository.findById(1L)).thenReturn(Optional.of(userSource));
        when(userRepository.findById(2L)).thenReturn(Optional.of(userTarget));

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfertAmount(1L, 2L, "Test", 100));
        assertTrue(exception.getMessage().contains("Solde insuffisant"));
    }

    /**
     * Vérifie que la méthode récupère correctement les transactions de l'utilisateur.
     */
    @Test
    void getTransactionsForUser_shouldReturnTransactionList() {
        // Arrange
        List<Transaction> mockList = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.findByUserSenderUserIdOrUserReceiverUserId(1L, 1L)).thenReturn(mockList);

        // Act
        List<Transaction> result = transactionService.getTransactionsForUser(1L);

        // Assert
        assertEquals(2, result.size());
        verify(transactionRepository).findByUserSenderUserIdOrUserReceiverUserId(1L, 1L);
    }
}
