package com.paymybuddy.service;


import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ContactRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.IContactService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContactService implements IContactService {

    private static final Logger logger = LogManager.getLogger(ContactService.class);

    private  final ContactRepository contactRepository;
    private final UserRepository userRepository;


    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }


    @Override
    public Contact addNewContact(String email) {
        // Récupération de l'utilisateur connecté via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();
        // Vérifier si le contact (ami) existe via son email
        Optional<User> userContact = userRepository.findByEmail(email);
        if (userContact.isEmpty()) {
            throw new NoSuchElementException("Utilisateur avec l'email " + email + " non trouvé.");
        }
        // empêcher un utilisateur d’ajouter lui-même comme contact.
        if (userId.equals(userContact.get().getUserId())) {
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même comme contact.");
        }
        // Vérifier que la relation n'existe pas déjà
        User ownerUser = userDetails.getUser();
        User friendUser = userContact.get();
        Optional<Contact> existingContact = contactRepository.findByOwnerIdUserAndFriendIdUser(ownerUser, friendUser);
        if (existingContact.isPresent()) {
            throw new IllegalStateException("Le contact existe déjà.");
        }
        // Créer et enregistrer la nouvelle relation
        Contact contact = new Contact();
        contact.setOwnerIdUser(ownerUser);
        contact.setFriendIdUser(friendUser);
        contact.setDateContact(LocalDateTime.now());

        return contactRepository.save(contact);
    }


    @Override
    public List<Contact> getAllContacts(Long userId) {
        // Récupération de l'utilisateur connecté via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();  // Récupérer l'objet User complet
        List<Contact> contacts = contactRepository.findByOwnerIdUser(user);
        return contacts;
    }


    public List<String> getFriendEmails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();
        List<Contact> contacts = getAllContacts(userId);

        // Extraire uniquement les emails des amis
        return contacts.stream()
                .map(contact -> contact.getFriendIdUser().getEmail())
                .collect(Collectors.toList());
    }

    //supprime contact
    @Override
    public void deleteContact(String email) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User owner = userDetails.getUser();

        Optional<User> friendOpt = userRepository.findByEmail(email);
        if (friendOpt.isEmpty()) {
            throw new NoSuchElementException("Aucun utilisateur trouvé avec l'email : " + email);
        }

        User friend = friendOpt.get();

        Optional<Contact> contactOpt = contactRepository.findByOwnerIdUserAndFriendIdUser(owner, friend);
        if (contactOpt.isEmpty()) {
            throw new NoSuchElementException("Ce contact n'existe pas dans votre liste.");
        }

        contactRepository.delete(contactOpt.get());
        logger.info("Contact supprimé : {} -> {}", owner.getEmail(), friend.getEmail());
    }


}

