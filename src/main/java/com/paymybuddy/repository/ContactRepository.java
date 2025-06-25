package com.paymybuddy.repository;

import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
//    Optional<User> findByEmail(String email);
//    Optional<Contact> findByOwnerIdUserAndFriendIdUser(User ownerUser, User friendUser);
//
//    List<Contact> findByUserOwnerId(long userId);
//
//    ///////////////
//    //Iterable<User> findByEmail(String email);
//
//    Iterable<User> findByFriendContactsFriendIdUser(User friendIdUser);//ownerContacts

}
