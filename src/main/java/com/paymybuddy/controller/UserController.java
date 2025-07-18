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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;


@Controller
@RequestMapping("/paymybuddy")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public UserController(IUserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    //affiche page web form login
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userLoginDto", new UserLoginDto());
        return "login";
    }

    //affiche page web form inscription
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        if (!model.containsAttribute("userRegisterDto")) {
            model.addAttribute("userRegisterDto", new UserRegisterDto());
        }
        return "register";
    }

    //affiche page web confirmation d'inscription
    @GetMapping("/register/confirmed")
    public String showConfirmationRegister() {
        return "confirmationRegister";
    }

    //creer un nouveau utilisateur
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("userRegisterDto") @Valid UserRegisterDto userRegisterDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {
            //confirmation que les mots de passe identique
        if (!userRegisterDto.getPassword().equals(userRegisterDto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.user", "Les mots de passe ne correspondent pas.");
        }


        if (result.hasErrors()) {
            // On stocke les erreurs et l'objet UserRegisterDto dans les flash attributes
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegisterDto", result);
            redirectAttributes.addFlashAttribute("userRegisterDto", userRegisterDto);
            // Redirection vers le formulaire en GET
            return "redirect:/paymybuddy/register";
        }

        userRegisterDto.setDateCreate(LocalDateTime.now());// ajouter la date de création

        try {
            userService.createUser(userRegisterDto);
        } catch (IllegalArgumentException e) {
            // Gestion des erreurs métiers (ex: email déjà utilisé)
            redirectAttributes.addFlashAttribute("userRegisterDto", userRegisterDto);
            redirectAttributes.addFlashAttribute("emailError", e.getMessage());
            // Redirection vers le formulaire en GET
            return "redirect:/paymybuddy/register";
        }

        return "redirect:/paymybuddy/register/confirmed";
    }

}
