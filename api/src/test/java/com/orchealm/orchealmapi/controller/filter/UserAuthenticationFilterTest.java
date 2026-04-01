package com.orchealm.orchealmapi.controller.filter;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserAuthenticationFilterTest {

    @Test
    void validTeamKeysKeepsValidKeysWithoutDuplicates() {
        assertEquals(List.of("TEAM_1", "PLATFORM"),
            UserAuthenticationFilter.validTeamKeys(List.of("TEAM_1", "PLATFORM", "TEAM_1")));
    }

    @Test
    void validTeamKeysIgnoresInvalidKeys() {
        assertEquals(List.of("TEAM_1"),
            UserAuthenticationFilter.validTeamKeys(
                Arrays.asList("TEAM_1", "team_2", "TEAM_1:EDITOR", "", null, "TEAM'); DROP TABLE teams; --")));
    }

    @Test
    void validTeamKeysReturnsEmptyListWithoutClaim() {
        assertEquals(List.of(), UserAuthenticationFilter.validTeamKeys(null));
    }
}
