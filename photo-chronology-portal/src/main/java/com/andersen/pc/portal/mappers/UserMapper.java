package com.andersen.pc.portal.mappers;

import com.andersen.pc.common.model.dto.request.UserCreationRequest;
import com.andersen.pc.common.model.dto.response.UserDto;
import com.andersen.pc.common.model.entity.User;
import com.andersen.pc.common.model.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Mapping(source = "userRoles", target = "roles", qualifiedByName = "mapUserRoles")
    public abstract UserDto dataToDto(User user);
    public abstract List<UserDto> dataListToDtoList(List<User> users);

    public abstract User dtoToData(UserCreationRequest userCreationRequest);

    @Named("mapUserRoles")
    protected Set<String> mapUserRoles(Set<UserRole> dataRoles) {
        return dataRoles.stream()
                .map(dataRole -> dataRole.getRole().getRoleName().getAuthority())
                .collect(Collectors.toSet());
    }
}
