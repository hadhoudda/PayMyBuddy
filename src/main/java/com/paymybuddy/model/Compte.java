package com.paymybuddy.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comptes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compte implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_compte")
    private int compteId;

    private double solde;

    //relation between table compte and user
    @ManyToOne
    @JoinColumn(name = "fk_id_user")
    @JsonBackReference
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
