package com.paymybuddy.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_user")
    private int userId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    private String email;

    private String password;

    //relation between table  compte and user
    @OneToMany(mappedBy = "user", // nom de l'attribut dans class Compte
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private Set<Compte> comptes = new HashSet<>();

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
