package com.paymybuddy.service;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service chargé de charger les détails d'un utilisateur pour Spring Security.
 * Recherche l'utilisateur par email et retourne un CustomUserDetails.
 */
@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LogManager.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    /**
     * Constructeur avec injection du repository utilisateur.
     *
     * @param userRepository repository pour accéder aux utilisateurs
     */
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Recherche un utilisateur par email et retourne ses détails pour Spring Security.
     *
     * @param email email de l'utilisateur
     * @return UserDetails contenant les informations de l'utilisateur
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé avec l'email : {}", email);
                    return new UsernameNotFoundException("L'utilisateur n'existe pas");
                });

        logger.info("Utilisateur trouvé avec l'email : {}", email);
        return new CustomUserDetails(user);
    }
}
