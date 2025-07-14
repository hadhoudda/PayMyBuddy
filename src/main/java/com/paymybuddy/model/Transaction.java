package com.paymybuddy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private Long transactionId;

    @Column(name = "description")
    @NotNull(message = "description can not be null")
    @Size(min = 8, max = 100, message = "La description doit faire entre 8 et 100 caractères")
    private String transactionDescription;

    @NotNull(message = "amount can not be null")
    @Column(name = "amount")
    private double transactionAmount;

    @Column(name = "date_transaction")
    private LocalDateTime transactionDate;

    //relation between table transaction and user
    @ManyToOne
    @JoinColumn(name = "user_id_sender", nullable = false)
    private User userSender;

    @ManyToOne
    @JoinColumn(name = "user_id_receiver", nullable = false)
    private User userReceiver;
}
