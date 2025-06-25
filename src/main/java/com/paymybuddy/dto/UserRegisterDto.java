package com.paymybuddy.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UserRegisterDto {
    private String userName;
    private String email;
    private String password;
}
