package com.paymybuddy.mapper;

import com.paymybuddy.dto.ContactDto;
import com.paymybuddy.dto.UserLoginDto;
import com.paymybuddy.model.Contact;
import com.paymybuddy.model.User;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface ContactMapper {
    ContactDto toDto(Contact contact);
    Contact toEntity(ContactDto dto);
}