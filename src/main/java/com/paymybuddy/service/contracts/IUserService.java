package com.paymybuddy.service.contracts;

import com.paymybuddy.model.User;

import java.util.List;

public interface IUserService {

    List<User> getAllUsers();
    User findUserById(int id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(int id);
}
