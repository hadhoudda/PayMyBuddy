package com.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/paymybuddy")
public class ContactController {
    @GetMapping("/relation")
    public String relation() {
        return "relation";
    }
//
//    private static final Logger logger = LogManager.getLogger(ContactController.class);
//
//    @Autowired
//    ContactServiceImpl contactService;
//    @Autowired
//    UserServiceImpl userService;
//
//
////
//    @GetMapping("/contact/test")
//    private ResponseEntity<Iterable<User>> getTestFriend(User user){
//        Iterable<User> userList= contactService.getFriendsByUserId(user.getUserId());
//        return ResponseEntity.ok((userList));
//    }
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
//    @PostMapping("/{id}")
//    public ResponseEntity<Contact> addNewContact(@PathVariable long id,
//                                                 @RequestParam String email) {
//        try {
//            Contact contact = contactService.createContact(id, email);
//            logger.info("Contact ajouté avec succès.");
//            return ResponseEntity.ok(contact);
//        } catch (NoSuchElementException | IllegalStateException e) {
//            logger.warn("Erreur lors de l'ajout du contact : {}", e.getMessage());
//            return ResponseEntity.badRequest().build();
//        } catch (Exception e) {
//            logger.error("Erreur serveur : {}", e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }


}
