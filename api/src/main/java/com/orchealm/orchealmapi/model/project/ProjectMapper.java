package com.orchealm.orchealmapi.model.project;

import com.orchealm.orchealmapi.persistence.model.Project;
import com.orchealm.orchealmapi.persistence.model.ProjectView;
import com.orchealm.orchealmapi.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teamKey", source = "projectRecord.teamKey")
    @Mapping(target = "parent", source = "projectRecord.parent")
    @Mapping(target = "organization", source = "projectRecord.organization")
    @Mapping(target = "name", source = "projectRecord.name")
    @Mapping(target = "color", source = "projectRecord.color")
    @Mapping(target = "textColor", source = "projectRecord.textColor")
    @Mapping(target = "createdBy", source = "user.externalId")
    @Mapping(target = "updatedBy", source = "user.externalId")
    @Mapping(target = "insertDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "rowVersion", ignore = true)
    Project toProject(User user, ProjectRecord projectRecord);

    @Mapping(target = "master", expression = "java(project.getParent() == null)")
    @Mapping(target = "fullName", expression = "java(project.getOrganization() + \" - \" + project.getName())")
    ProjectDTO toDto(Project project);

    @Mapping(target = "master", source = "isMaster")
    ProjectViewDTO toDto(ProjectView project);
}
