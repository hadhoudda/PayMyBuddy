package com.paymybuddy.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comptes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_compte")
    private int compteId;

    private double solde;

    //relation between table compte and user
    @ManyToOne
    @JoinColumn(name = "fk_id_user")
    private User user;

    //relation between table compte and transaction
    @OneToMany(mappedBy = "compteSender",// nom de l'attribut dans class Compte
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Transaction> transactionSenders = new HashSet<>();

    @OneToMany(mappedBy = "compteReciever",// nom de l'attribut dans class Compte
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Transaction> transactionRecievers = new HashSet<>();

}
