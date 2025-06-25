package com.paymybuddy.mapper;

import com.paymybuddy.dto.UserLoginDto;
import com.paymybuddy.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserLoginMapper {
    UserLoginDto toDto(User user);
    User toEntity(UserLoginDto dto);
}