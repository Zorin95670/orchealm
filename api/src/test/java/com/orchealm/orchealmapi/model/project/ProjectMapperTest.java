package com.orchealm.orchealmapi.model.project;

import com.orchealm.orchealmapi.persistence.model.Project;
import com.orchealm.orchealmapi.persistence.model.ProjectView;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMapperTest {

    private final ProjectMapper mapper = new ProjectMapperImpl();

    @Test
    void toDtoComputesMasterFromParentAndFullName() {
        var project = new Project();
        project.setOrganization("ACME");
        project.setName("Portail");

        var dto = mapper.toDto(project);

        assertTrue(dto.isMaster());
        assertEquals("ACME - Portail", dto.getFullName());

        project.setParent(UUID.randomUUID());

        assertFalse(mapper.toDto(project).isMaster());
    }

    @Test
    void viewToDtoMapsMaster() {
        var view = new ProjectView();
        view.setIsMaster(false);

        assertFalse(mapper.toDto(view).isMaster());

        view.setIsMaster(true);

        assertTrue(mapper.toDto(view).isMaster());
    }
}
