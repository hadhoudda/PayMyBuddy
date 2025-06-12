package com.paymybuddy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paymybuddy") //localhost:8080/paymybuddy
public class HomeController {

    @GetMapping(path = "/home")
    public String getHome(){
        return "page d'accueil";
    }
}
