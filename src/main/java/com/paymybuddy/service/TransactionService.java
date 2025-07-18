package com.paymybuddy.service;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.ITransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsable des opérations liées aux transactions.
 */
@Service
@Transactional
public class TransactionService implements ITransactionService {

    private static final Logger logger = LogManager.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Affiche les transactions d'un utilisateur avec pagination.
     *
     * @param userId ID de l'utilisateur
     * @param page   numéro de page (0-based)
     * @param size   taille de la page
     * @return Page contenant les transactions
     */
    @Override
    public Page<Transaction> displayTransaction(long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Affichage des transactions pour userId={} page={} size={}", userId, page, size);
        return transactionRepository.listTransactions(userId, pageable);
    }

    /**
     * Effectue un transfert entre deux utilisateurs, avec application de frais.
     *
     * @param idSource    ID de l'expéditeur
     * @param idCible     ID du destinataire
     * @param description Description de la transaction
     * @param montant     Montant à transférer
     */
    @Override
    public void transfertAmount(long idSource, long idCible, String description, double montant) {
        if (montant <= 0) {
            logger.warn("Montant invalide pour transfert : {}", montant);
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        User source = userRepository.findById(idSource)
                .orElseThrow(() -> {
                    logger.error("Utilisateur source introuvable avec ID={}", idSource);
                    return new EntityNotFoundException("Expéditeur non trouvé avec l'ID : " + idSource);
                });

        User cible = userRepository.findById(idCible)
                .orElseThrow(() -> {
                    logger.error("Utilisateur cible introuvable avec ID={}", idCible);
                    return new EntityNotFoundException("Destinataire non trouvé avec l'ID : " + idCible);
                });

        BigDecimal montantTransfert = BigDecimal.valueOf(montant);
        BigDecimal soldeSource = source.getSolde() != null ? source.getSolde() : BigDecimal.ZERO;

        // Calcul des frais (0.5%)
        BigDecimal tauxFrais = new BigDecimal("0.005");
        BigDecimal frais = montantTransfert.multiply(tauxFrais).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalDebite = montantTransfert.add(frais);

        if (soldeSource.compareTo(totalDebite) < 0) {
            logger.warn("Solde insuffisant pour l'utilisateur ID={} : Solde={}, Requis={}",
                    idSource, soldeSource, totalDebite);
            throw new IllegalArgumentException("Solde insuffisant pour couvrir le montant et les frais.");
        }

        // Mise à jour des soldes
        source.setSolde(soldeSource.subtract(totalDebite));
        BigDecimal soldeCible = cible.getSolde() != null ? cible.getSolde() : BigDecimal.ZERO;
        cible.setSolde(soldeCible.add(montantTransfert));

        userRepository.save(source);
        userRepository.save(cible);

        logger.info("Transfert effectué : {}€ de l'utilisateur ID={} vers ID={}. Frais={}€, Total débité={}",
                montant, idSource, idCible, frais, totalDebite);

        // Enregistrement de la transaction
        Transaction transaction = new Transaction();
        transaction.setUserSender(source);
        transaction.setUserReceiver(cible);
        transaction.setTransactionAmount(montant);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setTransactionDescription(description);

        transactionRepository.save(transaction);
        logger.info("Transaction enregistrée pour ID={} -> ID={}", idSource, idCible);
    }

    /**
     * Récupère toutes les transactions où l'utilisateur est expéditeur ou destinataire.
     *
     * @param userId ID de l'utilisateur
     * @return Liste des transactions
     */
    @Override
    public List<Transaction> getTransactionsForUser(Long userId) {
        logger.debug("Récupération des transactions pour l'utilisateur ID={}", userId);
        return transactionRepository.findByUserSenderUserIdOrUserReceiverUserId(userId, userId);
    }
}
