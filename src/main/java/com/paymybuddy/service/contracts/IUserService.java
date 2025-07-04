package com.paymybuddy.service.contracts;

import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User createUser(UserRegisterDto userDto);
    Optional<User> findUserByEmail(String email);
    //List<User> getAllUsers();
    //Optional<User> findUserById(long id);

    //User updateUser(User user);
    //Optional<User> findByEmail(String email);
    void verseSolde(double montant);
    void updateUserName(String userName);
    void deleteUser();
    //void transfertAmount(long idSource, long idCible, double montant);

}

