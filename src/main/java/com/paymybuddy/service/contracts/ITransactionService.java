package com.paymybuddy.service.contracts;

import com.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ITransactionService {

    Page<Transaction> displayTransaction(long userId, int page, int size);
    public void transfertAmount(long idSource, long idCible,String description, double montant);
    List<Transaction> getTransactionsForUser(Long userId);
}
