package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.team.TeamRecord;
import com.orchealm.orchealmapi.model.team.TeamVisibilityRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.Team;
import com.orchealm.orchealmapi.persistence.model.TeamQueryFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing teams.
 * Provides operations to create, read, update, and delete teams, while enforcing
 * RLS (Row-Level Security) permissions based on the provided {@link UserPrincipal}.
 */
public interface TeamService {
    /**
     * Creates a new team.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param teamRecord    the data for the team to create
     * @return the newly created {@link Team} entity
     */
    Team createTeam(UserPrincipal userPrincipal, TeamRecord teamRecord);

    /**
     * Retrieves a paginated list of teams, optionally filtered by specific fields.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param filters       a map of field names to list of values to filter teams (e.g., key, name)
     * @param pageable      pagination information
     * @return a paginated list of {@link Team} entities matching the filters and accessible by the user
     */
    Page<Team> findTeams(UserPrincipal userPrincipal, TeamQueryFilterDto filters, Pageable pageable);

    /**
     * Retrieves a single team by its unique identifier.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param key           the key of the team to retrieve
     * @return the {@link Team} entity
     */
    Team findTeamByKey(UserPrincipal userPrincipal, String key);

    /**
     * Updates an existing team with new data.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param key           the key of the team to update
     * @param teamRecord    the new data for the team
     * @return the updated {@link Team} entity
     */
    Team updateTeam(UserPrincipal userPrincipal, String key, TeamRecord teamRecord);

    /**
     * Deletes a team by its unique identifier.
     *
     * @param userPrincipal the authenticated user performing the operation
     * @param key           the key of the team to delete
     */
    void deleteTeam(UserPrincipal userPrincipal, String key);

    void createTeamIfNotExist(String teamKey);

    Team changeVisibility(UserPrincipal userPrincipal,
                          String key,
                          TeamVisibilityRecord visibilityRecord);
}
