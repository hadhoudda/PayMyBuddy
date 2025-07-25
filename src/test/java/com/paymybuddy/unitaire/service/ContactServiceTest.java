package com.paymybuddy.unitaire.service;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.ContactRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.ContactService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class ContactServiceTest {

    @MockitoBean
    private ContactRepository contactRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private ContactService contactService;

    @MockitoBean
    private Authentication authentication;

    @MockitoBean
    private SecurityContext securityContext;

    private User currentUser;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simuler utilisateur connecté
        currentUser = new User();
        currentUser.setUserId(1L);
        currentUser.setEmail("user1@example.com");

        customUserDetails = mock(CustomUserDetails.class);
        when(customUserDetails.getUser()).thenReturn(currentUser);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
    }

    @Test
    void addNewContact_shouldAddContact() {
        String friendEmail = "friend@example.com";

        User friendUser = new User();
        friendUser.setUserId(2L);
        friendUser.setEmail(friendEmail);

        when(userRepository.findByEmail(friendEmail)).thenReturn(Optional.of(friendUser));
        when(contactRepository.findByOwnerIdUserAndFriendIdUser(currentUser, friendUser)).thenReturn(Optional.empty());

        Contact savedContact = new Contact();
        savedContact.setOwnerIdUser(currentUser);
        savedContact.setFriendIdUser(friendUser);
        savedContact.setDateContact(LocalDateTime.now());

        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        Contact result = contactService.addNewContact(friendEmail);

        assertNotNull(result);
        assertEquals(currentUser, result.getOwnerIdUser());
        assertEquals(friendUser, result.getFriendIdUser());

        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void addNewContact_shouldThrowIfUserNotFound() {
        String friendEmail = "unknown@example.com";

        when(userRepository.findByEmail(friendEmail)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> contactService.addNewContact(friendEmail));

        assertTrue(ex.getMessage().contains("non trouvé"));
    }

    @Test
    void addNewContact_shouldThrowIfAddingSelf() {
        String myEmail = currentUser.getEmail();

        when(userRepository.findByEmail(myEmail)).thenReturn(Optional.of(currentUser));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contactService.addNewContact(myEmail));

        assertTrue(ex.getMessage().contains("vous-même"));
    }

    @Test
    void addNewContact_shouldThrowIfContactExists() {
        String friendEmail = "friend@example.com";

        User friendUser = new User();
        friendUser.setUserId(2L);
        friendUser.setEmail(friendEmail);

        Contact existingContact = new Contact();
        existingContact.setOwnerIdUser(currentUser);
        existingContact.setFriendIdUser(friendUser);

        when(userRepository.findByEmail(friendEmail)).thenReturn(Optional.of(friendUser));
        when(contactRepository.findByOwnerIdUserAndFriendIdUser(currentUser, friendUser)).thenReturn(Optional.of(existingContact));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> contactService.addNewContact(friendEmail));

        assertTrue(ex.getMessage().contains("existe déjà"));
    }

    @Test
    void getAllContacts_shouldReturnList() {
        Contact c1 = new Contact();
        Contact c2 = new Contact();

        when(contactRepository.findByOwnerIdUser(currentUser)).thenReturn(Arrays.asList(c1, c2));

        List<Contact> result = contactService.getAllContacts(currentUser.getUserId());

        assertEquals(2, result.size());
        verify(contactRepository).findByOwnerIdUser(currentUser);
    }

    @Test
    void getFriendEmails_shouldReturnEmails() {
        User friend1 = new User();
        friend1.setEmail("friend1@example.com");

        User friend2 = new User();
        friend2.setEmail("friend2@example.com");

        Contact contact1 = new Contact();
        contact1.setFriendIdUser(friend1);

        Contact contact2 = new Contact();
        contact2.setFriendIdUser(friend2);

        when(contactRepository.findByOwnerIdUser(currentUser)).thenReturn(Arrays.asList(contact1, contact2));

        List<String> emails = contactService.getFriendEmails();

        assertEquals(2, emails.size());
        assertTrue(emails.contains("friend1@example.com"));
        assertTrue(emails.contains("friend2@example.com"));
    }

    @Test
    void deleteContact_shouldDeleteContact() {
        String friendEmail = "friend@example.com";

        User friendUser = new User();
        friendUser.setUserId(2L);
        friendUser.setEmail(friendEmail);

        Contact contact = new Contact();
        contact.setOwnerIdUser(currentUser);
        contact.setFriendIdUser(friendUser);

        when(userRepository.findByEmail(friendEmail)).thenReturn(Optional.of(friendUser));
        when(contactRepository.findByOwnerIdUserAndFriendIdUser(currentUser, friendUser)).thenReturn(Optional.of(contact));

        doNothing().when(contactRepository).delete(contact);

        contactService.deleteContact(friendEmail);

        verify(contactRepository).delete(contact);
    }

    @Test
    void deleteContact_shouldThrowIfUserNotFound() {
        String friendEmail = "unknown@example.com";

        when(userRepository.findByEmail(friendEmail)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> contactService.deleteContact(friendEmail));

        assertTrue(ex.getMessage().contains("Aucun utilisateur trouvé"));
    }

    @Test
    void deleteContact_shouldThrowIfContactNotFound() {
        String friendEmail = "friend@example.com";

        User friendUser = new User();
        friendUser.setUserId(2L);
        friendUser.setEmail(friendEmail);

        when(userRepository.findByEmail(friendEmail)).thenReturn(Optional.of(friendUser));
        when(contactRepository.findByOwnerIdUserAndFriendIdUser(currentUser, friendUser)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(NoSuchElementException.class,
                () -> contactService.deleteContact(friendEmail));

        assertTrue(ex.getMessage().contains("n'existe pas"));
    }

    @Test
    void allMethods_shouldThrowSecurityExceptionIfNotAuthenticated() {
        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityException ex1 = assertThrows(SecurityException.class,
                () -> contactService.getAllContacts(currentUser.getUserId()));

        SecurityException ex2 = assertThrows(SecurityException.class,
                () -> contactService.getFriendEmails());

        SecurityException ex3 = assertThrows(SecurityException.class,
                () -> contactService.deleteContact("friend@example.com"));

        SecurityException ex4 = assertThrows(SecurityException.class,
                () -> contactService.addNewContact("friend@example.com"));

        assertEquals("Utilisateur non authentifié.", ex1.getMessage());
        assertEquals("Utilisateur non authentifié.", ex2.getMessage());
        assertEquals("Utilisateur non authentifié.", ex3.getMessage());
        assertEquals("Utilisateur non authentifié.", ex4.getMessage());
    }

    @Test
    void getAuthenticatedUser_shouldThrowIfAnonymousUser() {
        when(authentication.getPrincipal()).thenReturn("anonymousUser");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> contactService.getAllContacts(currentUser.getUserId()));

        assertEquals("Utilisateur non authentifié.", ex.getMessage());
    }

    @Test
    void getAuthenticatedUser_shouldThrowIfAuthenticationNull() {
        when(securityContext.getAuthentication()).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> contactService.getAllContacts(currentUser.getUserId()));

        assertEquals("Utilisateur non authentifié.", ex.getMessage());
    }
}
