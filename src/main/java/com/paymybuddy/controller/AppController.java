package com.paymybuddy.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur principal de l'application.
 * Gère la route d'entrée vers la page d'accueil (index).
 */
@Controller
public class AppController {

    /**
     * Gère les requêtes GET vers "/paymybuddy" et renvoie la page d'accueil.
     *
     * @return le nom de la vue "index"
     */
    @RequestMapping("/paymybuddy")
    public String index() {
        return "index";
    }
}
