package com.paymybuddy.model;

import jakarta.persistence.*;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transaction")
    private int idTransaction;

    @Column(name = "description")
    private String description;

    @Column(name = "amount")
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id_user", nullable = false)
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", referencedColumnName = "id_user", nullable = false)
    private User userReceiver;

    public int getIdTransaction() {
        return idTransaction;
    }

    public void setIdTransaction(int idTransaction) {
        this.idTransaction = idTransaction;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

}
