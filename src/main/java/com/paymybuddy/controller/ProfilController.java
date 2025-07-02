package com.paymybuddy.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/paymybuddy")
public class ProfilController {
    @GetMapping("/profil")
    public String profil(HttpServletResponse response) {
        //ajoute une gestion du cache HTTP pour les pages sensibles
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        return "profil";
    }

}
