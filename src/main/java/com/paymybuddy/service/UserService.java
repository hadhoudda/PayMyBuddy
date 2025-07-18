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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des utilisateurs.
 */
@Service
@Transactional
public class UserService implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserRegisterMapper userRegisterMapper;

    public UserService(UserRepository userRepository, UserRegisterMapper userRegisterMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userRegisterMapper = userRegisterMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Crée un nouvel utilisateur à partir des données d'inscription.
     *
     * @param userRegisterDto DTO contenant les informations de l'utilisateur
     * @return l'utilisateur créé et persisté
     */
    @Override
    public User createUser(UserRegisterDto userRegisterDto) {
        logger.info("Création d’un utilisateur : username='{}', email='{}'",
                userRegisterDto.getUserName(), userRegisterDto.getEmail());

        Optional<User> existingUser = userRepository.findByEmail(userRegisterDto.getEmail());
        if (existingUser.isPresent()) {
            logger.error("Échec création : email déjà existant.");
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
        }

        if (userRegisterDto.getUserName() == null || userRegisterDto.getUserName().trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est obligatoire");
        }
        if (userRegisterDto.getEmail() == null || userRegisterDto.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email est obligatoire");
        }
        if (userRegisterDto.getPassword() == null || userRegisterDto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe est obligatoire");
        }

        String encodedPassword = passwordEncoder.encode(userRegisterDto.getPassword());
        userRegisterDto.setPassword(encodedPassword);

        User user = userRegisterMapper.toEntity(userRegisterDto);
        user.setDateCreate(LocalDateTime.now());

        logger.info("Utilisateur enregistré avec succès : {}", user.getEmail());
        return userRepository.save(user);
    }

    /**
     * Recherche un utilisateur par son email.
     *
     * @param email l'email à rechercher
     * @return un Optional contenant l'utilisateur s'il existe
     */
    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Ajoute un montant au solde de l'utilisateur connecté.
     *
     * @param montant le montant à ajouter
     */
    @Override
    public void verseSolde(double montant) {
        if (montant <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à zéro.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Ajout de solde échoué : utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

            BigDecimal montantAjoute = BigDecimal.valueOf(montant);
            BigDecimal soldeActuel = user.getSolde() != null ? user.getSolde() : BigDecimal.ZERO;
            BigDecimal nouveauSolde = soldeActuel.add(montantAjoute).setScale(2, RoundingMode.HALF_UP);

            user.setSolde(nouveauSolde);
            userRepository.save(user);

            logger.info("Solde mis à jour avec succès pour utilisateur ID={}", userId);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            logger.error("Conflit de mise à jour concurrente : utilisateur ID={}", userId);
            throw new RuntimeException("Votre solde a été modifié par une autre opération. Veuillez réessayer.");
        }
    }

    /**
     * Met à jour le nom d'utilisateur de l'utilisateur connecté.
     *
     * @param userName le nouveau nom d'utilisateur
     */
    @Override
    public void updateUserName(String userName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.error("Modification username échouée : utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));

            user.setUserName(userName);
            userRepository.save(user);

            logger.info("Nom d'utilisateur mis à jour pour utilisateur ID={}", userId);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            logger.error("Conflit de mise à jour concurrente pour utilisateur ID={}", userId);
            throw new RuntimeException("Une autre opération a modifié votre compte. Veuillez réessayer.");
        }
    }

    /**
     * Supprime l'utilisateur connecté de la base de données.
     */
    @Override
    public void deleteUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.info("Suppression utilisateur échouée : utilisateur non authentifié.");
            throw new SecurityException("Utilisateur non authentifié.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUser().getUserId();

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

            userRepository.delete(user);

            logger.info("Utilisateur supprimé avec succès : ID={}", userId);
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            logger.error("Conflit de suppression (version) : utilisateur ID={}", userId);
            throw new RuntimeException("Une autre opération a modifié votre compte. Suppression annulée.");
        }
    }

    /**
     * Récupère la liste des adresses email des contacts d'un utilisateur.
     *
     * @param userId ID de l'utilisateur
     * @return liste des emails de ses amis/contacts
     */
    @Override
    public List<String> getContactsEmails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable."));

        return user.getOwnerContacts().stream()
                .map(contact -> contact.getFriendIdUser().getEmail())
                .toList();
    }
}
