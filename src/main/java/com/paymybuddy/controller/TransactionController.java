package com.paymybuddy.controller;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.dto.TransactionDto;
import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.UserService;
import com.paymybuddy.service.contracts.ITransactionService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Contrôleur gérant les transferts d'argent entre utilisateurs.
 */
@Controller
@RequestMapping("/paymybuddy")
public class TransactionController {

    private static final Logger logger = (Logger) LogManager.getLogger(TransactionController.class);
    private final ContactService contactService;
    private final ITransactionService transactionService;
    private final UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    public TransactionController(ContactService contactService, ITransactionService transactionService, UserService userService) {
        this.contactService = contactService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Affiche le formulaire de transfert et la liste des transactions de l'utilisateur connecté.
     *
     * @param model       modèle Spring pour passer les attributs à la vue
     * @param userDetails détails de l'utilisateur connecté
     * @return la vue "transfert"
     */
    @GetMapping("/transfert")
    public String showTransfertForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User currentUser = userDetails.getUser();

        model.addAttribute("transactionDto", new TransactionDto());
        model.addAttribute("contacts", userService.getContactsEmails(currentUser.getUserId()));

        List<Transaction> transactions = transactionService.getTransactionsForUser(currentUser.getUserId());
        model.addAttribute("transactions", transactions);

        return "transfert";
    }

    /**
     * Traite un transfert entre l'utilisateur connecté et un de ses contacts.
     *
     * @param transactionDto     données du formulaire
     * @param result             résultat de la validation
     * @param redirectAttributes permet d'ajouter des messages flash
     * @param userDetails        utilisateur actuellement connecté
     * @param model              modèle Spring
     * @return la vue à afficher ou la redirection
     */
    @PostMapping("/transfert")
    public String TransfertSolde(
            @ModelAttribute("transactionDto") @Valid TransactionDto transactionDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails userDetails,  // utilisateur connecté
            Model model) {

        if (result.hasErrors()) {
            return "transfert";
        }

        // Récupérer l'utilisateur connecté
        User sourceUser = userDetails.getUser();

        // Récupérer le destinataire
        User cibleUser = userRepository.findByEmail(transactionDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec cet email"));

        if (cibleUser == null) {
            result.rejectValue("email", "error.transactionDto", "Aucun utilisateur trouvé avec cet email.");
            return "transfert";
        }

        try {
            double montant = transactionDto.getTransactionAmount();

            // Calcul des frais (0.5%)
            BigDecimal montantTransfert = BigDecimal.valueOf(montant);
            BigDecimal tauxFrais = new BigDecimal("0.005");
            BigDecimal frais = montantTransfert.multiply(tauxFrais).setScale(2, RoundingMode.HALF_UP);

            // Effectuer le transfert
            transactionService.transfertAmount(
                    sourceUser.getUserId(),
                    cibleUser.getUserId(),
                    transactionDto.getTransactionDescription(),
                    montant);

            // Message de confirmation avec montant + frais
            String message = String.format(
                    "Transfert de %.2f € effectué avec succès. Frais de transfert : %.2f €.",
                    montantTransfert, frais);

            redirectAttributes.addFlashAttribute("successMessage", message);

            return "redirect:/paymybuddy/transfert";

        } catch (IllegalArgumentException | jakarta.persistence.EntityNotFoundException e) {
            result.rejectValue("transactionAmount", "error.transactionDto", e.getMessage());
            return "transfert";
        }
    }
}