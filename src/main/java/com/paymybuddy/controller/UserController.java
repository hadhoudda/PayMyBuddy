package com.paymybuddy.controller;

import com.paymybuddy.dto.UserDto;
import com.paymybuddy.model.User;
import com.paymybuddy.service.UserService;
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

    @Autowired
    private UserService userService;


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserDto());
        return "login"; // nom du fichier .html
    }

//    @GetMapping("/login")
//    public String login(Model model) {
//        model.addAttribute("users", userService.getAllUsers());
//        return "login";
//    }
//    @GetMapping("/utilisateur")
//    public  List<User> getUsers(){
//       return userService.getAllUsers();
//    }
//
//    @GetMapping("/utilisateur/{id}")
//    public ResponseEntity<User>  getUserById(@PathVariable int id){
//        User user = userService.findUserById(id);
//        if(user==null){
//            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
//        }
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }

    @PostMapping("/register")
    public ResponseEntity<User> postUser(@RequestBody User user){
        User userCreat = userService.createUser(user);
        if(userCreat.getEmail().equals(user.getEmail())){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } else {
            return new ResponseEntity<>(userCreat,HttpStatus.CREATED);
        }
    }

    @PutMapping
    public User updateUser(@RequestBody User user){
        return userService.updateUser(user);
    }

    @DeleteMapping("/utilisateur/{id}")
    public void deleteUser(@PathVariable int id){
        userService.deleteUser(id);
    }

    @GetMapping("/utilisateur/relation/{email}")
    public ResponseEntity<List<User>> getUserByEmail(@PathVariable String email) {
        try {
            List<User> userList = userService.findByEmail(email);
            if (userList.isEmpty()) {
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(userList, HttpStatus.OK);
            }

        } catch (Exception e) {
            return ResponseEntity.noContent().build();
        }
    }

}
