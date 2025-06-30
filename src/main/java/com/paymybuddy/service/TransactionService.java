package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.ITransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Transactional
public class TransactionService implements ITransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }



    @Override
    public Page<Transaction> displayTransaction(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.listTransactions(userId, pageable);
    }

    @Override
    public void transfertAmount(long idSource, long idCible, double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        User source = userRepository.findById(idSource)
                .orElseThrow(() -> new EntityNotFoundException("Expéditeur non trouvé avec l'ID : " + idSource));

        User cible = userRepository.findById(idCible)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID : " + idCible));

        BigDecimal montantTransfert = BigDecimal.valueOf(montant);
        BigDecimal soldeSource = source.getSolde() != null ? source.getSolde() : BigDecimal.ZERO;

        if (soldeSource.compareTo(montantTransfert) < 0) {
            throw new IllegalArgumentException("Solde insuffisant");
        }

        // Mise à jour des soldes
        source.setSolde(soldeSource.subtract(montantTransfert));
        cible.setSolde((cible.getSolde() != null ? cible.getSolde() : BigDecimal.ZERO).add(montantTransfert));

        userRepository.save(source);
        userRepository.save(cible);
        System.out.println("////////////////////////////");
        System.out.println(source);
        System.out.println(cible);
        System.out.println("////////////////////////////");
        // Enregistrement de la transaction
        Transaction transaction = new Transaction();
        transaction.setUserSender(source);
        transaction.setUserReceiver(cible);
        transaction.setTransactionAmount(montant);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionDescription("Transfert de " + montant + " de " + source.getUserName() + " à " + cible.getUserName());
        transactionRepository.save(transaction);
    }
}
