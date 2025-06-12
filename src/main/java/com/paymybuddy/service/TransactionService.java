package com.paymybuddy.service;


import com.paymybuddy.service.contracts.ITransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionService implements ITransactionService {
}
