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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

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
    public void transfertAmount(long idSource, long idCible, String description, double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        User source = userRepository.findById(idSource)
                .orElseThrow(() -> new EntityNotFoundException("Expéditeur non trouvé avec l'ID : " + idSource));

        User cible = userRepository.findById(idCible)
                .orElseThrow(() -> new EntityNotFoundException("Destinataire non trouvé avec l'ID : " + idCible));

        BigDecimal montantTransfert = BigDecimal.valueOf(montant);
        BigDecimal soldeSource = source.getSolde() != null ? source.getSolde() : BigDecimal.ZERO;

        // calcul du frais (0.5%)
        BigDecimal tauxFrais = new BigDecimal("0.005");
        BigDecimal frais = montantTransfert.multiply(tauxFrais).setScale(2, RoundingMode.HALF_UP);

        // mMontant total à débiter du compte source
        BigDecimal totalDebite = montantTransfert.add(frais);

        // Vérifie le solde
        if (soldeSource.compareTo(totalDebite) < 0) {
            throw new IllegalArgumentException("Solde insuffisant pour couvrir le montant et les frais.");
        }

        // mise à jour des soldes
        source.setSolde(soldeSource.subtract(totalDebite));
        BigDecimal soldeCible = cible.getSolde() != null ? cible.getSolde() : BigDecimal.ZERO;
        cible.setSolde(soldeCible.add(montantTransfert));

        userRepository.save(source);
        userRepository.save(cible);

        // 🔍 Log pour vérification
        System.out.println("////////////////////////////");
        System.out.println("Source : " + source);
        System.out.println("Cible : " + cible);
        System.out.println("Montant transféré : " + montantTransfert);
        System.out.println("Frais appliqué : " + frais);
        System.out.println("Montant débité : " + totalDebite);
        System.out.println("////////////////////////////");

        // ✅ Enregistrement de la transaction
        Transaction transaction = new Transaction();
        transaction.setUserSender(source);
        transaction.setUserReceiver(cible);
        transaction.setTransactionAmount(montant); // ce que le destinataire reçoit
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionDescription(description);
        transactionRepository.save(transaction);
    }


    @Override
    public List<Transaction> getTransactionsForUser(Long userId) {
        return transactionRepository.findByUserSenderUserIdOrUserReceiverUserId(userId, userId);
    }
}
