package com.paymybuddy.unitaire.service;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.mapper.UserRegisterMapper;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @MockitoBean
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private UserRegisterMapper userRegisterMapper;

    @Autowired
    private UserService userService;

    private UserRegisterDto userRegisterDto;
    private User user;

    private Authentication authentication;
    private SecurityContext securityContext;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUserName("Jean");
        userRegisterDto.setEmail("jean@example.com");
        userRegisterDto.setPassword("password");

        user = new User();
        user.setUserId(123L);
        user.setUserName("Jean");
        user.setEmail("jean@example.com");
        user.setPassword("encodedPassword");
        user.setDateCreate(LocalDateTime.now());
        user.setSolde(BigDecimal.valueOf(100.00));

        // Simulation d’un utilisateur authentifié via Spring Security
        authentication = mock(Authentication.class);
        customUserDetails = mock(CustomUserDetails.class);
        securityContext = mock(SecurityContext.class);

        when(customUserDetails.getUser()).thenReturn(user);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(customUserDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUser_shouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User result = userService.createUser(userRegisterDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jean", result.getUserName());
        assertEquals("jean@example.com", result.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findByEmail(userRegisterDto.getEmail())).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.createUser(userRegisterDto)
        );

        assertEquals("Un utilisateur avec cet email existe déjà", exception.getMessage());
    }

    @Test
    void createUser_shouldThrowExceptionWhenUserNameIsEmpty() {
        // Arrange
        userRegisterDto.setUserName("  ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRegisterDto));
    }

    @Test
    void createUser_shouldThrowExceptionWhenEmailIsEmpty() {
        // Arrange
        userRegisterDto.setEmail("");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRegisterDto));
    }

    @Test
    void createUser_shouldThrowExceptionWhenPasswordIsEmpty() {
        // Arrange
        userRegisterDto.setPassword("   ");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userRegisterDto));
    }

    @Test
    void findUserByEmail_shouldReturnUserIfExists() {
        // Arrange
        when(userRepository.findByEmail("jean@example.com")).thenReturn(Optional.of(user));

        // Act
        Optional<User> foundUser = userService.findUserByEmail("jean@example.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("Jean", foundUser.get().getUserName());
    }

    @Test
    void findUserByEmail_shouldReturnEmptyIfNotFound() {
        // Arrange
        when(userRepository.findByEmail("inconnu@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<User> foundUser = userService.findUserByEmail("inconnu@example.com");

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void verseSolde_shouldAddAmountToUserBalance() {
        double montant = 50.0;

        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Act
        userService.verseSolde(montant);

        // Assert
        BigDecimal expectedSolde = BigDecimal.valueOf(150.00).setScale(2, BigDecimal.ROUND_HALF_UP);
        assertEquals(expectedSolde, user.getSolde());
        verify(userRepository).save(user);
    }

    @Test
    void verseSolde_shouldThrowException_WhenMontantIsNegativeOrZero() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.verseSolde(0));
        assertThrows(IllegalArgumentException.class, () -> userService.verseSolde(-10));
    }

    @Test
    void verseSolde_shouldThrowException_WhenUserNotAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> userService.verseSolde(10));
        assertEquals("Utilisateur non authentifié.", exception.getMessage());
    }

    @Test
    void verseSolde_shouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.verseSolde(10));
        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void verseSolde_shouldThrowRuntimeException_OnOptimisticLockException() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        doThrow(new ObjectOptimisticLockingFailureException(User.class, 1))
                .when(userRepository).save(any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.verseSolde(10));
        assertEquals("Votre solde a été modifié par une autre opération. Veuillez réessayer.", exception.getMessage());
    }

    @Test
    void updateUserName_shouldUpdateSuccessfully() {
        String nouveauNom = "Pierre";
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Act
        userService.updateUserName(nouveauNom);

        // Assert
        assertEquals(nouveauNom, user.getUserName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUserName_shouldThrowException_WhenUserNotAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> userService.updateUserName("NouvelNom"));
        assertEquals("Utilisateur non authentifié.", exception.getMessage());
    }

    @Test
    void updateUserName_shouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserName("NomInexistant"));

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }

    @Test
    void updateUserName_shouldThrowRuntimeException_OnOptimisticLockException() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        doThrow(new ObjectOptimisticLockingFailureException(User.class, 1))
                .when(userRepository).save(any());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUserName("NomConcurrent"));

        assertEquals("Une autre opération a modifié votre compte. Veuillez réessayer.", exception.getMessage());
    }

    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser();

        // Assert
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_shouldThrowException_WhenUserNotAuthenticated() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        SecurityException exception = assertThrows(SecurityException.class, () -> userService.deleteUser());
        assertEquals("Utilisateur non authentifié.", exception.getMessage());
    }

    @Test
    void deleteUser_shouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.deleteUser());
        assertEquals("Utilisateur introuvable.", exception.getMessage());
    }

    @Test
    void deleteUser_shouldThrowRuntimeException_OnOptimisticLockException() {
        // Arrange
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        doThrow(new ObjectOptimisticLockingFailureException(User.class, user.getUserId()))
                .when(userRepository).delete(user);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser());
        assertEquals("Une autre opération a modifié votre compte. Suppression annulée.", exception.getMessage());
    }

    @Test
    void getContactsEmails_shouldReturnEmailsList() {
        // Arrange
        User friend1 = new User();
        friend1.setEmail("ami1@example.com");

        User friend2 = new User();
        friend2.setEmail("ami2@example.com");

        Contact contact1 = new Contact();
        contact1.setFriendIdUser(friend1);

        Contact contact2 = new Contact();
        contact2.setFriendIdUser(friend2);

        user.setOwnerContacts(Set.of(contact1, contact2));

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        // Act
        List<String> emails = userService.getContactsEmails(user.getUserId());

        // Assert
        assertEquals(2, emails.size());
        assertTrue(emails.contains("ami1@example.com"));
        assertTrue(emails.contains("ami2@example.com"));
    }

    @Test
    void getContactsEmails_shouldThrowException_WhenUserNotFound() {
        // Arrange
        Long invalidUserId = 999L;
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            userService.getContactsEmails(invalidUserId);
        });

        assertEquals("Utilisateur introuvable.", exception.getMessage());
    }
}
