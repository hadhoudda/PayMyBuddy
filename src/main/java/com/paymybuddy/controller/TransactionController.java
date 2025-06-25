package com.paymybuddy.controller;

import com.paymybuddy.model.Transaction;
import com.paymybuddy.service.contracts.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/paymybuddy")
public class TransactionController {

    private final ITransactionService transactionService;

    @Autowired
    public TransactionController(ITransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transfert")
    public String transfert() {
        return "transfert";
    }
    /**
     * Endpoint pour afficher les transactions d’un utilisateur
     * Exemple d’appel : GET /api/transactions/user/1?page=0&size=10
     */
    @GetMapping("/transactions/{userId}")
    public Page<Transaction> getUserTransactions(
            @PathVariable("userId") long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return transactionService.displayTransaction(userId, page, size);
    }

    //transfert d'argent
    @PutMapping("/transactions/{idUser}/{idCible}/{montant}")
    public ResponseEntity<String> transfertSolde(@PathVariable long idUser, @PathVariable long idCible, @PathVariable double montant){
        transactionService.transfertAmount(idUser,idCible, montant);
        return ResponseEntity.ok("solde transferet");
    }
}
