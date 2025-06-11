package com.paymybuddy.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comptes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Compte {

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
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Transaction> compteSenders = new HashSet<>();

    @OneToMany(mappedBy = "compteReciever",// nom de l'attribut dans class Compte
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Connection> compteReciever = new HashSet<>();

}
