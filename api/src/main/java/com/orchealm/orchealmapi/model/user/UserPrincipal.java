package com.orchealm.orchealmapi.model.user;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.persistence.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Authenticated user, with the authorizations read from the token. They are sent to PostgreSQL by
 * {@link com.orchealm.orchealmapi.persistence.interceptor.RlsInterceptor} for the RLS policies.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPrincipal extends User {

    /**
     * True when the user has the global ADMIN role.
     */
    private boolean admin;

    /**
     * Keys of the teams the user manages.
     */
    private List<String> managerOf;

    /**
     * Keys of the teams the user can view.
     */
    private List<String> viewerOf;

    /**
     * Keys of the teams the user can deploy to.
     */
    private List<String> deployerOf;

    /**
     * Checks that the user can create, update or delete the resources of a team (same rule as the RLS policies).
     *
     * @param teamKey the team key
     * @return true if the user is admin or manager of the team
     */
    public boolean canManage(final String teamKey) {
        return admin || (managerOf != null && managerOf.contains(teamKey));
    }

    /**
     * Checks that the user can plan, start or stop deployments of a team (same rule as the RLS policies).
     *
     * @param teamKey the team key
     * @return true if the user is admin, manager or deployer of the team
     */
    public boolean canDeploy(final String teamKey) {
        return canManage(teamKey) || (deployerOf != null && deployerOf.contains(teamKey));
    }

    /**
     * Rejects the request with a 403 when the user cannot manage the team.
     *
     * @param teamKey the team key
     * @throws ApiException 403 if {@link #canManage(String)} is false
     */
    public void assertCanManage(final String teamKey) {
        if (!canManage(teamKey)) {
            throw forbidden();
        }
    }

    /**
     * Rejects the request with a 403 when the user cannot deploy for the team.
     *
     * @param teamKey the team key
     * @throws ApiException 403 if {@link #canDeploy(String)} is false
     */
    public void assertCanDeploy(final String teamKey) {
        if (!canDeploy(teamKey)) {
            throw forbidden();
        }
    }

    private static ApiException forbidden() {
        return new ApiException(ErrorReference.forbidden());
    }
}
