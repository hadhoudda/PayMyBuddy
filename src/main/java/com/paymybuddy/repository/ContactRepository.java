package com.paymybuddy.repository;

import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    // Recherche un contact entre deux utilisateurs spécifiques : l'utilisateur propriétaire et son ami.
    Optional<Contact> findByOwnerIdUserAndFriendIdUser(User ownerUser, User friendUser);

    // Récupère tous les contacts où l'utilisateur donné est le propriétaire.
    List<Contact> findByOwnerIdUser(User ownerIdUser);

    // Récupère tous les contacts où l'utilisateur donné est l'ami (friend).
    List<Contact> findByFriendIdUser(User friendIdUser);

}
