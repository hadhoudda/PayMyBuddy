package com.paymybuddy.controller;

import com.paymybuddy.config.CustomUserDetails;
import org.springframework.ui.Model;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/paymybuddy")
public class TransactionController {

    private static final Logger logger = (Logger) LogManager.getLogger(TransactionController.class);
    private final ContactService contactService;
    private final ITransactionService transactionService;
    private  final UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    public TransactionController(ContactService contactService, ITransactionService transactionService, UserService userService) {
        this.contactService = contactService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

@GetMapping("/transfert")
public String showTransfertForm(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
    User currentUser = userDetails.getUser();

    model.addAttribute("transactionDto", new TransactionDto());
    model.addAttribute("contacts", userService.getContactsEmails(currentUser.getUserId()));

    List<Transaction> transactions = transactionService.getTransactionsForUser(currentUser.getUserId());
    model.addAttribute("transactions", transactions);

    return "transfert";
}

    //transfert d'argent
    @PostMapping("/transfert")
    public String TransfertSolde(
            @ModelAttribute("transactionDto") @Valid TransactionDto transactionDto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal CustomUserDetails userDetails,  // récupération directe de l'utilisateur connecté
            Model model) {

        if (result.hasErrors()) {
            return "transfert";
        }

        // Récupérer l'utilisateur connecté
        User sourceUser = userDetails.getUser();

        // Récupérer le destinataire si existe
        User cibleUser = userRepository.findByEmail(transactionDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec cet email"));

        if (cibleUser == null) {
            result.rejectValue("email", "error.transactionDto", "Aucun utilisateur trouvé avec cet email.");
            return "transfert";
        }

        try {
            transactionService.transfertAmount(
                    sourceUser.getUserId(),
                    cibleUser.getUserId(),
                    transactionDto.getTransactionDescription(),
                    transactionDto.getTransactionAmount());

            redirectAttributes.addFlashAttribute("successMessage", "Transfert effectué avec succès.");
            return "redirect:/paymybuddy/transfert";

        } catch (IllegalArgumentException | jakarta.persistence.EntityNotFoundException e) {
            result.rejectValue("transactionAmount", "error.transactionDto", e.getMessage());
            return "transfert";
        }
    }

        /**
         * Endpoint pour afficher les transactions d’un utilisateur
         * Exemple d’appel : GET /api/transactions/user/1?page=0&size=10
         */
//    @GetMapping("/transactions/{userId}")
//    public Page<Transaction> getUserTransactions(
//            @PathVariable("userId") long userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return transactionService.displayTransaction(userId, page, size);
//    }

        //transfert d'argent
//    @PutMapping("/transactions/{idUser}/{idCible}/{montant}")
//    public ResponseEntity<String> transfertSolde(@PathVariable long idUser, @PathVariable long idCible, @PathVariable double montant){
//        transactionService.transfertAmount(idUser,idCible, montant);
//        return ResponseEntity.ok("solde transferet");
//    }

}