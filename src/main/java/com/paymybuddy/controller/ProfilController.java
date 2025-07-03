package com.paymybuddy.controller;

import com.paymybuddy.model.User;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Controller
@RequestMapping("/paymybuddy")
public class ProfilController {

    private final IUserService userService;

    public ProfilController(IUserService userService) {
        this.userService = userService;
    }

//    @GetMapping("/profil")
//    public String showProfil(HttpServletResponse response, Model model) {
//        //ajoute une gestion du cache HTTP pour les pages sensibles
//        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
//        response.setHeader("Pragma", "no-cache");
//        response.setDateHeader("Expires", 0);
//        User user = userService.;
//        model.addAttribute("user", user);
//        return "profil";
//    }
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


}
