package com.orchealm.orchealmapi.model.team;

import com.orchealm.orchealmapi.persistence.model.Team;
import com.orchealm.orchealmapi.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "key", source = "teamRecord.key")
    @Mapping(target = "name", source = "teamRecord.name")
    @Mapping(target = "description", source = "teamRecord.description")
    @Mapping(target = "createdBy", source = "user.externalId")
    @Mapping(target = "updatedBy", source = "user.externalId")
    @Mapping(target = "isPublic", constant = "false")
    @Mapping(target = "insertDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Team toTeam(User user, TeamRecord teamRecord);

    @Mapping(target = "key", source = "key")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "createdBy", source = "createdBy")
    @Mapping(target = "updatedBy", source = "updatedBy")
    @Mapping(target = "insertDate", source = "insertDate")
    @Mapping(target = "updateDate", source = "updateDate")
    TeamDTO toDto(Team team);
}
