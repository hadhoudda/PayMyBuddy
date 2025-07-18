package com.paymybuddy.repository;

import com.paymybuddy.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    //pour affiche les transation que je le fait avec description
    @Query("SELECT o FROM Transaction o WHERE o.userSender.userId = :userId ORDER BY o.transactionDate DESC")
    Page<Transaction> listTransactions(@Param("userId") Long userId, Pageable pageable);

    List<Transaction> findByUserSenderUserIdOrUserReceiverUserId(Long senderId, Long receiverId);

}
