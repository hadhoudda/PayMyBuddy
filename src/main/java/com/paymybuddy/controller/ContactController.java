package com.paymybuddy.controller;

import com.paymybuddy.config.CustomUserDetails;
import com.paymybuddy.dto.ContactDto;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.Contact;
import com.paymybuddy.service.ContactService;
import com.paymybuddy.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/paymybuddy")
public class ContactController {

    private static final Logger logger = (Logger) LogManager.getLogger(ContactController.class);
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

//    @GetMapping("/relation")
//    public String relation(Model model) {
//        List<String> contacts = contactService.getFriendEmails();
//        logger.info("nbre de contacts : " + contacts.size());
//
//        if (!model.containsAttribute("contactDto")) {
//            model.addAttribute("contactDto", new ContactDto());
//        }
//
//        if (!model.containsAttribute("contacts")) {
//            model.addAttribute("contacts", contacts);
//        }
//
//        return "relation";
//    }

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

//@GetMapping("/friends")
//@ResponseBody
//public List<String> getFriendEmails() {
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    if (authentication == null || !authentication.isAuthenticated()) {
//        logger.error("Utilisateur non authentifié.");
//        throw new SecurityException("Utilisateur non authentifié.");
//    }
//
//    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//    Long userId = userDetails.getUser().getUserId();
//    List<Contact> contacts = contactService.getAllContacts(userId);
//
//    // récuperes tous les donnees des amis
//    // return contacts.stream()
//    //                .map(ContactDto::new)
//    //                .collect(Collectors.toList());
//
//    // Extraire uniquement les emails des amis
//    return contacts.stream()
//            .map(contact -> contact.getFriendIdUser().getEmail())
//            .collect(Collectors.toList());
//}
}

//
//    @GetMapping("/{id}")
//    public ResponseEntity<List<User>> getFriends(@PathVariable long id) {
//
//        List<User> userList = contactService.getAllContacts(id);
//        return ResponseEntity.ok(userList);
////
////        if (user.isEmpty()) {
////            logger.info("User not found with ID: " + id);
////            return ResponseEntity.notFound().build();
////        }
////
////        //List<User> friends = contactService.getFriendsByUserId(id);
////
////        if (friends.isEmpty()) {
////            logger.info("No friends found for user ID: " + id);
////            return ResponseEntity.noContent().build();
////        }
////
////        logger.info("Found " + friends.size() + " friends for user ID: " + id);
////        return ResponseEntity.ok(friends);
//    }
//
////
