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
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

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
        userPrincipal.assertCanManage(teamRecord.key());

        return repository.save(mapper.toTeam(userPrincipal, teamRecord));
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Team findTeamByKey(final UserPrincipal userPrincipal, final String key) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findByKey(key)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("key", key)));
    }

    @Override
    public Team updateTeam(final UserPrincipal userPrincipal, final String key, final TeamRecord teamRecord) {
        // findTeamByKey -> call securize
        var team = findTeamByKey(userPrincipal, key);

        userPrincipal.assertCanManage(team.getKey());

        if (!team.matchesRowVersion(teamRecord.rowVersion())) {
            throw new ApiException(ErrorReference.conflict());
        }

        team.setKey(teamRecord.key());
        team.setName(teamRecord.name());
        team.setDescription(teamRecord.description());
        team.setUpdatedBy(userPrincipal.getExternalId());

        return repository.save(team);
    }

    @Override
    public void deleteTeam(final UserPrincipal userPrincipal, final String key) {
        // findTeamById -> call securize
        var Team = findTeamByKey(userPrincipal, key);

        userPrincipal.assertCanManage(Team.getKey());

        repository.delete(Team);
    }

    @Override
    public void createTeamsIfNotExist(final Set<String> teamKeys) {
        // ponytail: one INSERT per team on every request, add a cache if it ever shows up in profiling
        rlsInterceptor.securizeWithAdmin();

        teamKeys.forEach(repository::insertIfNotExists);
    }

    @Override
    public Team changeVisibility(final UserPrincipal userPrincipal,
                                 final String key,
                                 final TeamVisibilityRecord visibilityRecord) {
        var team = findTeamByKey(userPrincipal, key);

        if (!team.matchesRowVersion(visibilityRecord.rowVersion())) {
            throw new ApiException(ErrorReference.conflict());
        }

        team.setIsPublic(visibilityRecord.isPublic());
        team.setUpdatedBy(userPrincipal.getExternalId());

        return repository.save(team);
    }
}
