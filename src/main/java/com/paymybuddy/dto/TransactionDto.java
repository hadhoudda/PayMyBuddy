package com.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class TransactionDto {

    @NotBlank(message = "L'email du destinataire est obligatoire.")
    @Email(message = "Email invalide.")
    private String email;

    @NotBlank(message = "La description de la transaction est obligatoire.")
    private String transactionDescription;

    @Positive(message = "Le montant doit être supérieur à zéro.")
    private double transactionAmount;

    private LocalDateTime transactionDate = LocalDateTime.now();
}