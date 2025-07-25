package com.paymybuddy.controller;

import com.paymybuddy.dto.ContactDto;
import com.paymybuddy.service.ContactService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/paymybuddy")
public class ContactController {

    private static final Logger logger = (Logger) LogManager.getLogger(ContactController.class);
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }


    @GetMapping("/relation")
    public String relation(Model model) {
        if (!model.containsAttribute("contactDto")) {
            model.addAttribute("contactDto", new ContactDto());
        }
        return "relation";
    }

    @PostMapping("/relation")
    public String addContact(
            @ModelAttribute("contactDto") @Valid ContactDto contactDto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Ajouter la date de création
        contactDto.setDateCreate(LocalDateTime.now());

        // Vérifie les erreurs de validation
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.contactDto", result);
            redirectAttributes.addFlashAttribute("contactDto", contactDto);
            return "redirect:/paymybuddy/relation";
        }

        try {
            // Appel au service pour ajouter un nouveau contact
            contactService.addNewContact(contactDto.getEmail());
        } catch (IllegalArgumentException | IllegalStateException | NoSuchElementException e) {
            // Gestion des erreurs
            redirectAttributes.addFlashAttribute("contactDto", contactDto);
            redirectAttributes.addFlashAttribute("emailError", e.getMessage());
            return "redirect:/paymybuddy/relation";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Contact ajouté avec succès !");//message affiche au vue
        return "redirect:/paymybuddy/relation";
    }

    @PostMapping("/contact/delete")
    public String deleteContact(@RequestParam String email,
                                RedirectAttributes redirectAttributes) {
        try {
            contactService.deleteContact(email);
            redirectAttributes.addFlashAttribute("successMessage", "Contact supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/paymybuddy/profil";
    }
}

