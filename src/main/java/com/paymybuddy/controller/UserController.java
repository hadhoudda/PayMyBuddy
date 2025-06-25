package com.paymybuddy.controller;

import com.paymybuddy.dto.UserLoginDto;
import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserServiceImpl;
import com.paymybuddy.service.contracts.IUserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@Controller
@RequestMapping("/paymybuddy")
public class UserController {

    private static final Logger logger = LogManager.getLogger(UserController.class);

    private final IUserService userServiceImpl;

    public UserController(IUserService userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    //    @GetMapping("/login")
//    public String showLoginForm(Model model) {
//        model.addAttribute("user", new UserLoginDto());
//        return "login"; // nom du fichier .html
//    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new UserLoginDto());
        return "login";
    }

    @GetMapping("/connecte")// ajoute methode de connecte au service
    private ResponseEntity<String> connectUser(){
        return ResponseEntity.ok("user connecte");
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        List<User> userList = userServiceImpl.getAllUsers();
        if (userList.isEmpty()){
            logger.info("User not found");
            return ResponseEntity.noContent().build();
        }else {
            logger.info("Users successfully retrieved: " + userList.size() + " users");
            return ResponseEntity.ok(userList);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<User> addUser(@RequestBody UserRegisterDto userDto){
        User userCreate = userServiceImpl.createUser(userDto) ;
        return ResponseEntity.ok(userCreate);
    }

    // ajoute solde
    @PutMapping("/solde/{idUser}/{montant}")
    public ResponseEntity<String> addSolde(@PathVariable long idUser, @PathVariable double montant){
        userServiceImpl.verseSolde(idUser, montant);
        return ResponseEntity.ok("solde ajoute");
    }




//
//    @GetMapping("/utilisateur/{id}")
//    public ResponseEntity<User>  getUserById(@PathVariable int id){
//        User user = userService.findUserById(id);
//        if(user==null){
//            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }



    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        try {
            User updatedUser = userServiceImpl.updateUser(user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id) {
        try {
            userServiceImpl.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }

//    @GetMapping("/utilisateur/relation/{email}")
//    public ResponseEntity<List<User>> getUserByEmail(@PathVariable String email) {
//        try {
//            List<User> userList = userService.findByEmail(email);
//            if (userList.isEmpty()) {
//                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//            } else {
//                return new ResponseEntity<>(userList, HttpStatus.OK);
//            }
//
//        } catch (Exception e) {
//            return ResponseEntity.noContent().build();
//        }
//    }

}
