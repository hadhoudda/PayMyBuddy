package com.paymybuddy.service.contracts;

import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User createUser(UserRegisterDto userDto);
    Optional<User> findUserByEmail(String email);
    List<String> getContactsEmails(Long userId);
    void verseSolde(double montant);
    void updateUserName(String userName);
    void deleteUser();
}

