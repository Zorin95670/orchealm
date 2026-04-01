package com.orchealm.orchealmapi.model.environment;

import com.orchealm.orchealmapi.persistence.model.Environment;
import com.orchealm.orchealmapi.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "environmentRecord.name")
    @Mapping(target = "teamKey", source = "environmentRecord.teamKey")
    @Mapping(target = "shortName", source = "environmentRecord.shortName")
    @Mapping(target = "position", source = "environmentRecord.position")
    @Mapping(target = "createdBy", source = "user.externalId")
    @Mapping(target = "updatedBy", source = "user.externalId")
    @Mapping(target = "insertDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Environment toEnvironment(User user, EnvironmentRecord environmentRecord);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "teamKey", source = "teamKey")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "shortName", source = "shortName")
    @Mapping(target = "position", source = "position")
    @Mapping(target = "insertDate", source = "insertDate")
    @Mapping(target = "updateDate", source = "updateDate")
    EnvironmentDTO toDto(Environment environment);
}
