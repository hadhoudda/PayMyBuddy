package com.paymybuddy.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;


import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class UserRegisterDto {
    @NotBlank(message = "Le nom d'utilisateur est obligatoire.")
    @Size(min = 4, max = 30, message = "Le nom d'utilisateur doit faire entre 4 et 30 caractères")
    private String userName;

    @Column(unique = true)
    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "Format de mail invalide.")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 6, max = 12, message = "Le mot de passe doit faire entre 6 et 12 caractères")
    private String password;

    @Transient // ne sera pas enregistré en bdd
    @NotBlank(message = "Le mot de passe doit être identique.")
    @Size(min = 6, max = 12, message = "Le mot de passe doit faire entre 6 et 12 caractères")
    private String confirmPassword;

    private LocalDateTime dateCreate;
}
