package com.orchealm.orchealmapi.model.user;

import com.orchealm.orchealmapi.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "externalId", source = "user.externalId")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "insertDate", source = "user.insertDate")
    @Mapping(target = "updateDate", source = "user.updateDate")
    @Mapping(target = "globalRoles", source = "globalRoles")
    @Mapping(target = "teams", source = "teams")
    UserPrincipal toUserPrincipal(User user, String globalRoles, String teams);
}
