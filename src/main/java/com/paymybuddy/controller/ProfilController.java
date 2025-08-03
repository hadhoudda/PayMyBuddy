package com.paymybuddy.controller;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Contrôleur gérant les fonctionnalités liées au profil utilisateur :
 * affichage du profil
 * ajout de solde
 * modification du nom d'utilisateur
 * suppression du compte
 */
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

    /**
     * Affiche la page de profil de l'utilisateur connecté.
     * Empêche la mise en cache de la page pour des raisons de sécurité.
     *
     * @param response    objet HttpServletResponse pour ajouter des en-têtes
     * @param model       modèle pour passer les attributs à la vue
     * @param userDetails détails de l'utilisateur connecté
     * @return la vue "profil" ou redirection si l'utilisateur est non authentifié
     */
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

        // Si l'utilisateur n'est pas trouvé
        if (optionalUser.isEmpty()) {
            return "redirect:/paymybuddy";
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);

        // Ajout des contacts pour affichage dans la modale
        List<Contact> contacts = contactService.getAllContacts(user.getUserId());
        model.addAttribute("contacts", contacts);

        logger.info("Connexion de : {}", email);
        return "profil";
    }

    /**
     * Ajoute du solde au compte utilisateur.
     *
     * @param montant            montant à ajouter
     * @param redirectAttributes permet d'afficher un message de confirmation ou d'erreur
     * @return redirection vers la page de profil
     */
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

    /**
     * Modifie le nom d'utilisateur de l'utilisateur connecté.
     *
     * @param userName           nouveau nom d'utilisateur
     * @param redirectAttributes permet d'afficher un message de confirmation ou d'erreur
     * @return redirection vers la page de profil
     */
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

    /**
     * Supprime le compte utilisateur connecté.
     *
     * @param redirectAttributes permet d'afficher un message de confirmation ou d'erreur
     * @return redirection vers l'accueil
     */
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