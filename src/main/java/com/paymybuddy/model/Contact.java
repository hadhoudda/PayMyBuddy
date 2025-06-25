package com.paymybuddy.model;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id_owner", "user_id_friend"})
})//evite les doublons du contact en base des donnees
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_contact")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contactId;

    @Column(name = "date_contact")
    private LocalDateTime dateContact;

    //relation between table connection and user
    @ManyToOne
    @JoinColumn(name = "user_id_owner", nullable = false)
    private User ownerIdUser;

    @ManyToOne
    @JoinColumn(name = "user_id_friend", nullable = false)
    private User friendIdUser;

}
