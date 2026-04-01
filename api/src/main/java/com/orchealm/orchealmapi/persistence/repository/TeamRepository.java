package com.orchealm.orchealmapi.persistence.repository;

import com.orchealm.orchealmapi.persistence.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID>, JpaSpecificationExecutor<Team> {
    Optional<Team> findByKey(String key);

    /**
     * Creates the team with the given key if it does not exist yet. Safe under concurrency.
     *
     * @param key the team key
     */
    @Modifying
    @Query(value = """
        INSERT INTO teams (key, name, created_by, updated_by)
        VALUES (:key, :key, 'SYSTEM', 'SYSTEM')
        ON CONFLICT (key) DO NOTHING
        """, nativeQuery = true)
    void insertIfNotExists(@Param("key") String key);
}
