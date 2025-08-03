package com.paymybuddy.controller;

import com.paymybuddy.dto.UserLoginDto;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

/**
 * Contrôleur chargé de la gestion des utilisateurs :
 * affichage et traitement de l'inscription
 * affichage du formulaire de connexion
 * affichage de la confirmation d'inscription
 */
@Controller
@RequestMapping("/paymybuddy")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserController(IUserService userService,
                          PasswordEncoder passwordEncoder,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Affiche la page de connexion.
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "login";
    }

    /**
     * Affiche le formulaire d'inscription.
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("userRegisterDto")) {
            model.addAttribute("userRegisterDto", new UserRegisterDto());
        }
        return "register";
    }

    /**
     * Affiche la page de confirmation d'inscription.
     */
    @GetMapping("/register/confirmed")
    public String showConfirmationRegister() {
        return "confirmationRegister";
    }

    /**
     * Traite l'inscription d'un nouvel utilisateur.
     */
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("userRegisterDto") @Valid UserRegisterDto userRegisterDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        logger.info("Tentative d'inscription pour l'email: {}", userRegisterDto.getEmail());

        // Vérifie si les mots de passe sont identiques
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas.");
        }

        // Si erreurs de validation, on renvoie vers le formulaire
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterDto", result);
            redirectAttributes.addFlashAttribute("userRegisterDto", userRegisterDto);
            return "redirect:/paymybuddy/register";
        }

        userRegisterDto.setDateCreate(LocalDateTime.now());

        try {
            userService.createUser(userRegisterDto);
            logger.info("Inscription réussie pour l'email: {}", userRegisterDto.getEmail());
        } catch (IllegalArgumentException e) {
            logger.warn("Erreur lors de l'inscription: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("userRegisterDto", userRegisterDto);
            redirectAttributes.addFlashAttribute("emailError", e.getMessage());
            return "redirect:/paymybuddy/register";
        }

        return "redirect:/paymybuddy/register/confirmed";
    }
}
