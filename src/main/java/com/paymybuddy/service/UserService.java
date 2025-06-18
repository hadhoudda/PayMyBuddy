package com.paymybuddy.service;


import com.paymybuddy.model.Transaction;
import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.service.contracts.IUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService implements IUserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            return null;
        } else {
            return optionalUser.get();
        }
    }

    @Override
    public User createUser(User user) {
        try {
            //verifie si l'utilisateur exixte ou non avant la creation
            List<User> userList = userRepository.findByEmail(user.getEmail());

            if (userList.isEmpty()) {
//                for (Transaction transaction : user.getTransactions()) {
//                    transaction.;
//                }
                return userRepository.save(user);
            }else {
                logger.error("user is exist");
                return null;
            }
        } catch (Exception e) {
            logger.error("Error adding user: {}", e.getMessage());
            return null;
        }

    }


    @Override
    public User updateUser(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getUserId());
        if (optionalUser.isEmpty()){
            return null;
        }else {
            return userRepository.save(user);
        }

    }

    @Override
    public void deleteUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if(optionalUser.isEmpty()){
            System.out.println("user not found");
        } else {
            userRepository.deleteById(id);
        }
    }

    @Override
    public List<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
