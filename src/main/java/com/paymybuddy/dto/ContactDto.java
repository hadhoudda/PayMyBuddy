package com.paymybuddy.dto;


import com.paymybuddy.model.Contact;
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

    private Long contactId;
    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "Format de mail invalide.")
    private String email;

    private LocalDateTime dateCreate;

    public ContactDto() { }

    public ContactDto(Contact contact) {
        this.contactId = contact.getContactId();
        this.dateCreate = contact.getDateContact();
        this.email = contact.getFriendIdUser().getEmail();
    }

}
