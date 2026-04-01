package com.orchealm.orchealmapi.persistence.repository;

import com.orchealm.orchealmapi.persistence.model.Deployment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface DeploymentRepository extends JpaRepository<Deployment, UUID>, JpaSpecificationExecutor<Deployment> {
}
