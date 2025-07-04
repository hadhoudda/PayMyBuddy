package com.paymybuddy.service.contracts;

import com.paymybuddy.dto.ContactDto;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


public interface IContactService {

    Contact addNewContact(String email);

//    List<User> getAllContacts(long idUser);
//
//    void deleteContact(long idUser, String email);
//    Optional<Contact> findByIdUserAndEmail(long idUser, String emailContact);
//
//
//    boolean contactExists(String email);
//    Contact createNewContactByEmail(long ownerUserId, String friendEmail);
}