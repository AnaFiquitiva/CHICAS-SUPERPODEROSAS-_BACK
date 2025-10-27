package eci.edu.dosw.proyecto.utils.mappers;


import eci.edu.dosw.proyecto.dto.*;
import eci.edu.dosw.proyecto.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toUserResponse(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    User toUser(UserResponse userResponse);

    UserBasicResponse toUserBasicResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);
    List<UserBasicResponse> toUserBasicResponseList(List<User> users);

    default String map(Role role) {
        return role != null ? role.getName() : null;
    }

    default Role map(String roleName) {
        if (roleName == null) return null;
        Role role = new Role();
        role.setName(roleName);
        return role;
    }

}