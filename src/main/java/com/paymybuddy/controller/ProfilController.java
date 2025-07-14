package com.paymybuddy.controller;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/paymybuddy")
public class ProfilController {

    private static final Logger logger = LogManager.getLogger(ProfilController.class);
    private final IUserService userService;
    private final ContactService contactService;

    public ProfilController(IUserService userService, ContactService contactService) {
        this.userService = userService;
        this.contactService = contactService;
    }


//    @GetMapping("/profil")
//    public String showProfil(
//            HttpServletResponse response,
//            Model model,
//            @AuthenticationPrincipal UserDetails userDetails) {
//
//        // empêcher le cache du navigateur
//        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setDateHeader("Expires", 0);
//        // Vérifier si l'utilisateur est connecté
//        if (userDetails == null) {
//            return "redirect:/paymybuddy";
//        }
//
//        String email = userDetails.getUsername();
//        Optional<User> optionalUser = userService.findUserByEmail(email);
//        logger.info("Profil demandé pour : {}", email);
//
//        //if (optionalUser.isPresent()) {
//        if (optionalUser.isEmpty()) {
//            return "redirect:/paymybuddy";
//        }
//        model.addAttribute("user", optionalUser.get());
//        logger.info("Connexion de : " + userDetails.getUsername());
//        return "profil";
//    }

    @GetMapping("/profil")
    public String showProfil(
            HttpServletResponse response,
            Model model,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // Empêcher le cache du navigateur
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Vérifier si l'utilisateur est connecté
        if (userDetails == null || userDetails.getUser() == null) {
            return "redirect:/paymybuddy";
        }

        String email = userDetails.getUsername();
        Optional<User> optionalUser = userService.findUserByEmail(email);
        logger.info("Profil demandé pour : {}", email);

        if (optionalUser.isEmpty()) {
            return "redirect:/paymybuddy";
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        // Charger et injecter les contacts pour la modale
        List<Contact> contacts = contactService.getAllContacts(user.getUserId());
        model.addAttribute("contacts", contacts);

        logger.info("Connexion de : {}", email);
        return "profil";
    }

    // ajoute solde
    @PostMapping("/profil/solde")
    public String addSolde(@RequestParam("montant") double montant,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.verseSolde(montant);
            redirectAttributes.addFlashAttribute("success", "Solde ajouté avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paymybuddy/profil";
    }

    // modifier username
    @PostMapping("/profil/username")
    public String updateUserName(@RequestParam("username") String userName,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.updateUserName(userName);
            redirectAttributes.addFlashAttribute("success", "UserName modifié avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paymybuddy/profil";
    }

    // supprime compte
    @PostMapping("/profil/delete")
    public String deleteUser(RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser();
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès !");
            return "redirect:/paymybuddy";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/paymybuddy";
        }
    }

}
