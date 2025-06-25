package com.paymybuddy.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long userId;

    @Column(name = "user_name")
    @NotNull(message = "userName can not be null")
    private String userName;

    private String email;

    private String password;

    private BigDecimal solde= BigDecimal.ZERO;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;


    //relation between table  transaction and user
    @OneToMany(mappedBy = "userSender", // nom de l'attribut dans class Transaction
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Transaction> transactionsSender = new HashSet<>();

    @OneToMany(mappedBy = "userReceiver", // nom de l'attribut dans class Transaction
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Transaction> transactionsReceiver = new HashSet<>();


    //relation between table  user and connection
    @OneToMany(mappedBy = "ownerIdUser",// nom de l'attribut dans class Connection
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Contact> ownerContacts = new HashSet<>();

    @OneToMany(mappedBy = "friendIdUser",// nom de l'attribut dans class Connection
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<Contact> friendContacts = new HashSet<>();

}
