package com.paymybuddy.service;


import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.mapper.UserRegisterMapper;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    private final UserRegisterMapper userRegisterMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserRegisterMapper userRegisterMapper) {
        this.userRepository = userRepository;
        this.userRegisterMapper = userRegisterMapper;
    }


    //ajouter nouvel utilisateur
    @Override
    public User createUser(UserRegisterDto userRegisterDto) {
        Optional<User> existingUser = userRepository.findByEmail(userRegisterDto.getEmail());
        logger.info("Création user, userName = '{}', email = '{}'", userRegisterDto.getUserName(), userRegisterDto.getEmail());
        if (existingUser.isPresent()) {
            logger.error("Impossible de créer, un utilisateur avec cet email existe déjà");
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        // Validation avant la conversion
        if (userRegisterDto.getUserName() == null || userRegisterDto.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom  est obligatoire");
        }
        if (userRegisterDto.getEmail() == null || userRegisterDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mail est obligatoire");
        }
        if (userRegisterDto.getPassword() == null || userRegisterDto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        // Crypter le mot de passe
        String encodedPassword = passwordEncoder.encode(userRegisterDto.getPassword());
        userRegisterDto.setPassword(encodedPassword);
        // on convertit en entité (avec le mot de passe hashé)
        User user = userRegisterMapper.toEntity(userRegisterDto);
        logger.info("Utilisateur ajouté avec succès : {}", user.getEmail());
        user.setDateCreate(LocalDateTime.now());//enregistre date de creation
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        Optional<User> userOptional= userRepository.findByEmail(email);
        return userOptional;
    }

    // ajouter solde
    @Override
    public void verseSolde(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        // Récupération de l'utilisateur connecté via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

            BigDecimal montantAjoute = BigDecimal.valueOf(montant);
            BigDecimal soldeActuel = user.getSolde() != null ? user.getSolde() : BigDecimal.ZERO;
            BigDecimal nouveauSolde = soldeActuel.add(montantAjoute);

            user.setSolde(nouveauSolde);
            userRepository.save(user);
            logger.info("Solde mis à jour avec succès pour l'utilisateur ID {}", userId);

        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            logger.error("Conflit de mise à jour concurrente pour l'utilisateur ID {}", userId);
            throw new RuntimeException("Votre solde a été modifié par une autre opération. Veuillez réessayer.");
        }
    }

    //modifier username
    @Override
    public void updateUserName(String userName) {
        // Récupération de l'utilisateur connecté via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

            // Mise à jour userName
            user.setUserName(userName);
            userRepository.save(user);

            logger.info("Username modifié avec succès pour l'utilisateur ID {}", userId);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            logger.error("Conflit de mise à jour concurrente sur le user ID {}", userId);
            throw new RuntimeException("Une autre opération a modifié votre compte. Veuillez réessayer.");
        }
    }

    @Override
    @Transactional
    public void deleteUser() {
        // Récupération de l'utilisateur connecté via SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.info("Utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();

        try {
            // Recharge l'utilisateur dans la même transaction
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

            // Suppression — le champ @Version sera vérifié
            userRepository.delete(user);

            logger.info("Utilisateur supprimé avec succès (ID {}).", userId);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            logger.error("Conflit de version lors de la suppression de l'utilisateur ID {}", userId);
            throw new RuntimeException("Une autre opération a modifié votre compte. Suppression annulée.");
        }
    }

    @Override
    public List<String> getContactsEmails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        return user.getOwnerContacts().stream()
                .map(contact -> contact.getFriendIdUser().getEmail())  // accéder au User ami
                .toList();
    }
}


