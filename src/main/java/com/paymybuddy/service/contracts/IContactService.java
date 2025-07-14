package com.paymybuddy.service.contracts;

import com.paymybuddy.model.Contact;

import java.util.List;


public interface IContactService {

    Contact addNewContact(String email);
    List<Contact> getAllContacts(Long userId);
    List<String> getFriendEmails();
    void deleteContact(String email);

}