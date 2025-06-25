package com.paymybuddy.controller;


import com.paymybuddy.service.contracts.IUserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {



    @RequestMapping("/paymybuddy")
    public String index() {
        return "index";
    }
}
