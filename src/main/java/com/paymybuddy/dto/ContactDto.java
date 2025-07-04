package com.paymybuddy.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class ContactDto {

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "Format de mail invalide.")
    private String email;

    private LocalDateTime dateCreate;
}
