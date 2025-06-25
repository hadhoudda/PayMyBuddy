package com.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class UserLoginDto {
    private String mail;
    private String password;

}
