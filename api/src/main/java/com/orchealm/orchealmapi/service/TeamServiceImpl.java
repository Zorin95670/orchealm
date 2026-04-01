package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.model.team.TeamMapper;
import com.orchealm.orchealmapi.model.team.TeamRecord;
import com.orchealm.orchealmapi.model.team.TeamVisibilityRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.interceptor.RlsInterceptor;
import com.orchealm.orchealmapi.persistence.model.Team;
import com.orchealm.orchealmapi.persistence.model.TeamQueryFilterDto;
import com.orchealm.orchealmapi.persistence.repository.TeamRepository;
import io.github.zorin95670.specification.SpringQueryFilterSpecification;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of {@link TeamService} responsible for managing teams.
 *
 * <p>This service applies Row-Level Security (RLS) via {@link RlsInterceptor} for every method
 * to ensure that users can only access or modify teams they have permissions for.
 *
 * <p>All methods are transactional and run within a Spring-managed transaction.
 */
@Slf4j
@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    /**
     * Interceptor responsible for setting RLS context variables in the current transaction
     * based on the authenticated {@link UserPrincipal}.
     */
    private final RlsInterceptor rlsInterceptor;

    /**
     * Repository for CRUD operations on {@link Team} entities.
     */
    private final TeamRepository repository;

    /**
     * Mapper for converting between {@link TeamRecord} and {@link Team} entities.
     */
    private final TeamMapper mapper;

    private final ConcurrentHashMap<String, Boolean> createdTeams = new ConcurrentHashMap<>();

    /**
     * Constructs a new {@link TeamServiceImpl}.
     *
     * @param rlsInterceptor the interceptor for setting RLS context variables
     * @param repository     the repository for accessing team entities
     * @param mapper         the mapper for converting between DTOs and entities
     */
    @Autowired
    public TeamServiceImpl(final RlsInterceptor rlsInterceptor,
                           final TeamRepository repository,
                           final TeamMapper mapper) {
        this.rlsInterceptor = rlsInterceptor;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Team createTeam(final UserPrincipal userPrincipal, final TeamRecord teamRecord) {
        rlsInterceptor.securize(userPrincipal);

        var team = repository.save(mapper.toTeam(userPrincipal, teamRecord));

        createdTeams.put(teamRecord.key(), true);

        return team;
    }

    @Override
    public Page<Team> findTeams(final UserPrincipal userPrincipal,
                                final TeamQueryFilterDto filters,
                                final Pageable pageable) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findAll(
            new SpringQueryFilterSpecification<>(Team.class, filters),
            pageable
        );
    }

    @Override
    public Team findTeamByKey(final UserPrincipal userPrincipal, final String key) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findByKey(key)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("key", key)));
    }

    @Override
    public Team updateTeam(final UserPrincipal userPrincipal, final String key, final TeamRecord teamRecord) {
        // findTeamByKey -> call securize
        var team = findTeamByKey(userPrincipal, key);

        team.setKey(teamRecord.key());
        team.setName(teamRecord.name());
        team.setDescription(teamRecord.description());
        team.setUpdatedBy(userPrincipal.getExternalId());

        var createdTeam = repository.save(team);
        createdTeams.remove(key);
        createdTeams.put(teamRecord.key(), true);

        return createdTeam;
    }

    @Override
    public void deleteTeam(final UserPrincipal userPrincipal, final String key) {
        // findTeamById -> call securize
        var Team = findTeamByKey(userPrincipal, key);

        repository.delete(Team);
        createdTeams.remove(key);
    }

    @Transactional
    public void createTeamIfNotExist(String teamKey) {
        if (createdTeams.containsKey(teamKey)) {
            return;
        }

        synchronized (teamKey.intern()) {
            rlsInterceptor.securizeWithAdmin();

            if (!repository.existsByKey(teamKey)) {
                Team team = new Team();
                team.setKey(teamKey);
                team.setName(teamKey);
                team.setCreatedBy("ADMIN");
                team.setUpdatedBy("ADMIN");

                repository.save(team);
                log.info("Team {} created", teamKey);
            }

            createdTeams.put(teamKey, true);
        }
    }

    @Override
    public Team changeVisibility(final UserPrincipal userPrincipal,
                                 final String key,
                                 final TeamVisibilityRecord visibilityRecord) {
        var team = findTeamByKey(userPrincipal, key);

        team.setIsPublic(visibilityRecord.isPublic());

        return repository.save(team);
    }
}
