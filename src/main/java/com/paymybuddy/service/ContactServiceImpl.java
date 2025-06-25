package com.paymybuddy.service;


import com.paymybuddy.dto.ContactDto;
import com.paymybuddy.mapper.ContactMapper;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ContactRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.IContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactServiceImpl implements IContactService {
//
////    @Autowired
////    ContactRepository contactRepository;
//    @Autowired
//    private UserServiceImpl userService;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    ContactServiceImpl contactService;
//    private ContactMapper contactMapper;
//
////public Iterable<User> getFriendsUser(User user){
////    return contactRepository.findByFriendContactsFriendIdUser(user);
////}
//
////    public List<User> getFriendsByUserId(Long userId) {
////        List<Contact> contacts = contactRepository.findByUserOwnerId(userId);
////        return contacts.stream()
////                .map(Contact::getFriendIdUser)
////                .collect(Collectors.toList());
////    }
//
////    @Override
////    public List<Contact> getAllContacts(long idUser) {
////        return getFriendsByUserId(i);
////    }
//
////    @Override
////    public List<User> getAllContacts(long idUser) {
////        Optional<User> user = userService.findUserById(idUser);
////        System.out.println(user.toString());
////        List<Contact> contacts = contactRepository.findByUserOwnerId(idUser);
////        System.out.println(contacts.toString());
////        //List<long> listIdContact =
////        return List.of();
////    }
//
////    @Override
////    public Contact createContact(long idUser, String email) {
////        // Vérifier si l'utilisateur propriétaire existe
////        Optional<User> userOptional = userService.findUserById(idUser);
////        if (userOptional.isEmpty()) {
////            throw new NoSuchElementException("Utilisateur avec l'ID " + idUser + " non trouvé.");
////        }
////
////        // Vérifier si le contact (ami) existe via son email
////        Optional<User> friendUserOptional = userRepository.findByEmail(email);
////        if (friendUserOptional.isEmpty()) {
////            throw new NoSuchElementException("Utilisateur avec l'email " + email + " non trouvé.");
////        }
////
////        User ownerUser = userOptional.get();
////        User friendUser = friendUserOptional.get();
////
////        // Vérifier que la relation n'existe pas déjà
////        Optional<Contact> existingContact = contactRepository.findByOwnerIdUserAndFriendIdUser(ownerUser, friendUser);
////        if (existingContact.isPresent()) {
////            throw new IllegalStateException("Le contact existe déjà.");
////        }
////
////        // Créer et enregistrer la nouvelle relation
////        Contact contact = new Contact();
////        contact.setOwnerIdUser(ownerUser);
////        contact.setFriendIdUser(friendUser);
////        contact.setDateContact(LocalDateTime.now());
////
////        return contactRepository.save(contact);
////    }
//
//
//    @Override
//    public List<User> getAllContacts(long idUser) {
//        return List.of();
//    }
//
//    @Override
//    public Contact createContact(long idUser, String email) {
//        return null;
//    }
//
//    @Override
//    public void deleteContact(long idUser, String email) {
//
//    }
//
//    @Override
//    public Optional<Contact> findByIdUserAndEmail(long idUser, String emailContact) {
//        return Optional.empty();
//    }
//
//    @Override
//    public boolean contactExists(String email) {
//        return false;
//    }
//
//    @Override
//    public Contact createNewContactByEmail(long ownerUserId, String friendEmail) {
//        return null;
//    }
}
