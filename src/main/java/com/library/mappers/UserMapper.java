package com.library.mappers;

import com.library.dto.UserDTO;
import com.library.entity.User;
import com.library.entity.enums.ERole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    @Mapping(target = "role", expression = "java(stringToRole(userDTO.role()))")
    User toEntity(UserDTO userDTO);
    List<UserDTO> toDTO(List<User> userList);
    List<User> toEntity(List<UserDTO> userDTOList);
    default ERole stringToRole(String role) {
        if (role == null) {
            return ERole.USER;  // Дефолтная роль, если роль не указана
        }
        try {
            return ERole.valueOf(role.toUpperCase());  // Преобразуем строку в ERole
        } catch (IllegalArgumentException e) {
            return ERole.USER;  // Если строка не является допустимым значением, используем дефолтную роль
        }
    }
}