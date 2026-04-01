package com.orchealm.orchealmapi.persistence.repository;

import com.orchealm.orchealmapi.persistence.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID>, JpaSpecificationExecutor<Project> {
    Optional<Project> findProjectByOrganizationAndName(String organization, String name);
}
