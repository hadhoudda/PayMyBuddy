package com.paymybuddy.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;


@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_transaction")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transactionId;

    @Column(name = "description")
    private String transactionDescription;

    @Column(name = "amount")
    private double transactionAmount;

    @Column(name = "date_transaction")
    private LocalDateTime transactionDate;

    //relation between table transaction and compte
    @ManyToOne
    //@JoinColumn(name = "fk_id_compte_sender")
    private Compte compteSender;

    @ManyToOne
    //@JoinColumn(name = "fk_id_compte_reciever")
    private Compte compteReciever;
}
