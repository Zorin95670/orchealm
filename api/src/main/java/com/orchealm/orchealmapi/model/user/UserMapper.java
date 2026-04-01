package com.orchealm.orchealmapi.model.user;

import com.orchealm.orchealmapi.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "externalId", source = "user.externalId")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "insertDate", source = "user.insertDate")
    @Mapping(target = "updateDate", source = "user.updateDate")
    @Mapping(target = "admin", source = "admin")
    @Mapping(target = "managerOf", source = "managerOf")
    @Mapping(target = "viewerOf", source = "viewerOf")
    @Mapping(target = "deployerOf", source = "deployerOf")
    UserPrincipal toUserPrincipal(User user,
                                  boolean admin,
                                  List<String> managerOf,
                                  List<String> viewerOf,
                                  List<String> deployerOf);
}
