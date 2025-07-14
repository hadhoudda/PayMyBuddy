package com.paymybuddy.repository;

import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByOwnerIdUserAndFriendIdUser(User ownerUser, User friendUser);
    //List<Contact> findByUserOwnerId(Long userId);

    // récupérer tous les contacts où l'utilisateur est le propriétaire
    List<Contact> findByOwnerIdUser(User ownerIdUser);

    // trouver les contacts où l'utilisateur est l'ami (friend)
    List<Contact> findByFriendIdUser(User friendIdUser);

}
