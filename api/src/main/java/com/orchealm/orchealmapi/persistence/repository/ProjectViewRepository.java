package com.orchealm.orchealmapi.persistence.repository;

import com.orchealm.orchealmapi.persistence.model.ProjectView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ProjectViewRepository extends JpaRepository<ProjectView, UUID>, JpaSpecificationExecutor<ProjectView> {
}
