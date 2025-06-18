package com.paymybuddy.controller;

import com.paymybuddy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RequestMapping("/paymybuddy") //localhost:8080/paymybuddy
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String home(Model model) {
        //model.addAttribute("users", userService.getAllUsers());
        return "home";
    }
}
