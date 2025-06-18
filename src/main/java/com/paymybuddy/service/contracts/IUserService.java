package com.paymybuddy.service.contracts;

import com.paymybuddy.model.User;

import java.util.List;

public interface IUserService {

    //methode Crud classique
    List<User> getAllUsers();
    User findUserById(long id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(long id);

    //methode crud avance
    List<User> findByEmail(String email);


}
