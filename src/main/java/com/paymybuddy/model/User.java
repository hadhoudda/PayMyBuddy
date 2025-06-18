package com.paymybuddy.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
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
    private long userId;

    @Column(name = "user_name")
    @NotNull(message = "lastName can not be null")
    private String userName;

    private String email;

    private String password;

    private BigDecimal solde;

    @Column(name = "date_create")
    private LocalDateTime dateCreate;

    //relation between table  transaction and user
    @OneToMany(mappedBy = "userSender", // nom de l'attribut dans class Transaction
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Transaction> transactionsSender = new HashSet<>();
    @OneToMany(mappedBy = "userReciever", // nom de l'attribut dans class Transaction
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Transaction> transactionsReciever = new HashSet<>();

    //relation between table  user and connection
    @OneToMany(mappedBy = "ownerUser",// nom de l'attribut dans class Connection
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Connection> ownerConnections = new HashSet<>();

    @OneToMany(mappedBy = "friendUser",// nom de l'attribut dans class Connection
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Connection> friendConnections = new HashSet<>();

}
