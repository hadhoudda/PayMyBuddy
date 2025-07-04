package com.paymybuddy.controller;

import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;
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
import java.util.Optional;

@Controller
@RequestMapping("/paymybuddy")
public class ProfilController {

    private static final Logger logger = LogManager.getLogger(ProfilController.class);
    private final IUserService userService;

    public ProfilController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profil")
    public String showProfil(
            HttpServletResponse response,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        String email = userDetails.getUsername();
        Optional<User> optionalUser = userService.findUserByEmail(email);

        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
        } else {
            return "redirect:/paymybuddy";
        }

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

    // modifier username
    @PostMapping("/profil/delete")
    public String deleteUser( RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser();
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé  avec succès !");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/paymybuddy";
    }

}
