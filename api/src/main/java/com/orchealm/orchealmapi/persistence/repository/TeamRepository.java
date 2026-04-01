package com.orchealm.orchealmapi.persistence.repository;

import com.orchealm.orchealmapi.persistence.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID>, JpaSpecificationExecutor<Team> {
    Optional<Team> findByKey(String key);

    Boolean existsByKey(String key);
}
