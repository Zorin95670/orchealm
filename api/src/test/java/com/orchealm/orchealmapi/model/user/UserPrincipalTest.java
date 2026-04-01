package com.orchealm.orchealmapi.model.user;

import com.orchealm.orchealmapi.model.error.ApiException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserPrincipalTest {

    private static UserPrincipal user(final boolean admin,
                                      final List<String> managerOf,
                                      final List<String> viewerOf,
                                      final List<String> deployerOf) {
        return UserPrincipal.builder()
            .admin(admin)
            .managerOf(managerOf)
            .viewerOf(viewerOf)
            .deployerOf(deployerOf)
            .build();
    }

    @Test
    void adminCanManageAndDeployEveryTeam() {
        var admin = user(true, List.of(), List.of(), List.of());

        assertTrue(admin.canManage("ANY"));
        assertTrue(admin.canDeploy("ANY"));
    }

    @Test
    void managerCanManageAndDeployOnlyHisTeams() {
        var manager = user(false, List.of("TEAM_1"), List.of(), List.of());

        assertTrue(manager.canManage("TEAM_1"));
        assertTrue(manager.canDeploy("TEAM_1"));
        assertFalse(manager.canManage("TEAM_2"));
        assertFalse(manager.canDeploy("TEAM_2"));
    }

    @Test
    void deployerCanDeployButNotManage() {
        var deployer = user(false, List.of(), List.of(), List.of("PLATFORM"));

        assertTrue(deployer.canDeploy("PLATFORM"));
        assertFalse(deployer.canManage("PLATFORM"));
    }

    @Test
    void viewerCanNeitherManageNorDeploy() {
        var viewer = user(false, List.of(), List.of("TEAM_1"), List.of());

        assertFalse(viewer.canManage("TEAM_1"));
        assertFalse(viewer.canDeploy("TEAM_1"));
    }

    @Test
    void userWithoutListsHasNoTeamRights() {
        var user = user(false, null, null, null);

        assertFalse(user.canManage("TEAM_1"));
        assertFalse(user.canDeploy("TEAM_1"));
    }

    @Test
    void assertCanManageThrowsForbidden() {
        var viewer = user(false, List.of(), List.of("TEAM_1"), List.of());

        var exception = assertThrows(ApiException.class, () -> viewer.assertCanManage("TEAM_1"));
        assertEquals(403, exception.getError().getStatusCode());
    }
}
