package com.paymybuddy.mapper;


import com.paymybuddy.dto.UserRegisterDto;
import com.paymybuddy.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRegisterMapper {

    UserRegisterDto toDto(User user);
    User toEntity(UserRegisterDto userRegisterDto);
}
