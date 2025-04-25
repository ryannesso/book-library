package com.library.mappers;

import com.library.dto.UserDTO;
import com.library.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
    List<UserDTO> toDTO(List<User> userList);
    List<User> toEntity(List<UserDTO> userDTOList);
}
