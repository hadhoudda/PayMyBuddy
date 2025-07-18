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

/**
 * Service de gestion des contacts (amis) pour les utilisateurs.
 * Permet d'ajouter, supprimer et récupérer les contacts d'un utilisateur authentifié.
 */
@Service
@Transactional
public class ContactService implements IContactService {

    private static final Logger logger = LogManager.getLogger(ContactService.class);

    private final ContactRepository contactRepository;
    private final UserRepository userRepository;

    /**
     * Constructeur avec injection des repositories nécessaires.
     *
     * @param contactRepository repository pour gérer les contacts
     * @param userRepository    repository pour gérer les utilisateurs
     */
    public ContactService(ContactRepository contactRepository, UserRepository userRepository) {
        this.contactRepository = contactRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ajoute un nouveau contact (ami) pour l'utilisateur connecté à partir de l'email du contact.
     *
     * @param email email du contact à ajouter
     * @return le contact ajouté
     * @throws SecurityException         si l'utilisateur n'est pas authentifié
     * @throws NoSuchElementException   si aucun utilisateur ne correspond à l'email donné
     * @throws IllegalArgumentException si l'utilisateur tente de s'ajouter lui-même
     * @throws IllegalStateException    si le contact existe déjà
     */
    @Override
    public Contact addNewContact(String email) {
        User currentUser = getAuthenticatedUser();

        Optional<User> userContact = userRepository.findByEmail(email);
        if (userContact.isEmpty()) {
            logger.warn("Tentative d'ajout de contact non trouvé avec l'email : {}", email);
            throw new NoSuchElementException("Utilisateur avec l'email " + email + " non trouvé.");
        }

        if (currentUser.getUserId().equals(userContact.get().getUserId())) {
            logger.warn("Utilisateur {} a tenté de s'ajouter lui-même comme contact.", currentUser.getEmail());
            throw new IllegalArgumentException("Vous ne pouvez pas vous ajouter vous-même comme contact.");
        }

        Optional<Contact> existingContact = contactRepository.findByOwnerIdUserAndFriendIdUser(currentUser, userContact.get());
        if (existingContact.isPresent()) {
            logger.warn("Contact déjà existant entre {} et {}", currentUser.getEmail(), email);
            throw new IllegalStateException("Le contact existe déjà.");
        }

        Contact contact = new Contact();
        contact.setOwnerIdUser(currentUser);
        contact.setFriendIdUser(userContact.get());
        contact.setDateContact(LocalDateTime.now());

        Contact savedContact = contactRepository.save(contact);
        logger.info("Nouveau contact ajouté : {} -> {}", currentUser.getEmail(), email);
        return savedContact;
    }

    /**
     * Récupère tous les contacts de l'utilisateur connecté.
     *
     * @param userId identifiant de l'utilisateur (non utilisé ici, authentification utilisée)
     * @return liste des contacts de l'utilisateur
     * @throws SecurityException si l'utilisateur n'est pas authentifié
     */
    @Override
    public List<Contact> getAllContacts(Long userId) {
        User currentUser = getAuthenticatedUser();
        return contactRepository.findByOwnerIdUser(currentUser);
    }

    /**
     * Récupère la liste des emails des amis (contacts) de l'utilisateur connecté.
     *
     * @return liste des emails des contacts
     * @throws SecurityException si l'utilisateur n'est pas authentifié
     */
    public List<String> getFriendEmails() {
        User currentUser = getAuthenticatedUser();

        List<Contact> contacts = contactRepository.findByOwnerIdUser(currentUser);

        return contacts.stream()
                .map(contact -> contact.getFriendIdUser().getEmail())
                .collect(Collectors.toList());
    }

    /**
     * Supprime un contact (ami) de la liste de contacts de l'utilisateur connecté, par email.
     *
     * @param email email du contact à supprimer
     * @throws SecurityException       si l'utilisateur n'est pas authentifié
     * @throws NoSuchElementException  si l'utilisateur contact n'existe pas
     * @throws NoSuchElementException  si la relation de contact n'existe pas
     */
    @Override
    public void deleteContact(String email) {
        User currentUser = getAuthenticatedUser();

        Optional<User> friendOpt = userRepository.findByEmail(email);
        if (friendOpt.isEmpty()) {
            logger.warn("Suppression impossible, utilisateur introuvable avec l'email : {}", email);
            throw new NoSuchElementException("Aucun utilisateur trouvé avec l'email : " + email);
        }

        Optional<Contact> contactOpt = contactRepository.findByOwnerIdUserAndFriendIdUser(currentUser, friendOpt.get());
        if (contactOpt.isEmpty()) {
            logger.warn("Suppression impossible, contact inexistant entre {} et {}", currentUser.getEmail(), email);
            throw new NoSuchElementException("Ce contact n'existe pas dans votre liste.");
        }

        contactRepository.delete(contactOpt.get());
        logger.info("Contact supprimé : {} -> {}", currentUser.getEmail(), email);
    }

    /**
     * Récupère l'utilisateur actuellement authentifié dans le contexte de sécurité.
     *
     * @return l'utilisateur authentifié
     * @throws SecurityException si aucun utilisateur n'est authentifié
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == "anonymousUser") {
            logger.error("Utilisateur non authentifié ou session invalide.");
            throw new SecurityException("Utilisateur non authentifié.");
        }

        return ((CustomUserDetails) authentication.getPrincipal()).getUser();
    }
}
