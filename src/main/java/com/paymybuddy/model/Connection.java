package com.paymybuddy.model;


import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "connections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Connection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id_connection")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int connectionId;

    @Column(name = "date_connection")
    private LocalDateTime dateConnection;

    //relation between table connection and user
    @ManyToOne
    //@JoinColumn(name = "fk_id_user_owner", nullable = false)
    private User ownerUser;

    @ManyToOne
    //@JoinColumn(name = "fk_id_user_friend", nullable = false)
    private User friendUser;

}
