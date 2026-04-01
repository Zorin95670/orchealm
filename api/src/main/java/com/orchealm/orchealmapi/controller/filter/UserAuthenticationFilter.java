package com.orchealm.orchealmapi.controller.filter;

import com.orchealm.orchealmapi.config.JwtClaimsProperties;
import com.orchealm.orchealmapi.model.user.UserMapper;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.Team;
import com.orchealm.orchealmapi.persistence.model.User;
import com.orchealm.orchealmapi.service.TeamService;
import com.orchealm.orchealmapi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final UserService service;
    private final UserMapper mapper;
    private final TeamService teamService;
    private final JwtClaimsProperties claims;

    /**
     * Keeps only the valid team keys of a team claim (see {@link Team#isValidKey}).
     *
     * @param teamKeys the raw claim value, may be null
     * @return the valid team keys, without duplicates
     */
    public static List<String> validTeamKeys(final List<String> teamKeys) {
        if (teamKeys == null) {
            return List.of();
        }

        return teamKeys.stream()
            .filter(teamKey -> {
                if (Team.isValidKey(teamKey)) {
                    return true;
                }

                log.warn("Ignoring invalid team key '{}' from token", teamKey);
                return false;
            })
            .distinct()
            .toList();
    }

    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request,
                                    final @NonNull HttpServletResponse response,
                                    final @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            // No bearer token: let Spring Security reject the request with a 401.
            filterChain.doFilter(request, response);
            return;
        }

        String externalId = jwt.getClaimAsString(claims.externalId());
        String name = jwt.getClaimAsString(claims.name());
        String email = jwt.getClaimAsString(claims.email());
        List<String> roles = jwt.getClaimAsStringList(claims.roles());
        boolean admin = roles != null && roles.contains("ADMIN");
        List<String> managerOf = validTeamKeys(jwt.getClaimAsStringList(claims.managerOf()));
        List<String> viewerOf = validTeamKeys(jwt.getClaimAsStringList(claims.viewerOf()));
        List<String> deployerOf = validTeamKeys(jwt.getClaimAsStringList(claims.deployerOf()));

        log.debug("Authenticated '{}' (admin: {}) manager of {}, viewer of {}, deployer of {}",
            externalId, admin, managerOf, viewerOf, deployerOf);

        Set<String> teamKeys = new HashSet<>(managerOf);
        teamKeys.addAll(viewerOf);
        teamKeys.addAll(deployerOf);

        if (!teamKeys.isEmpty()) {
            teamService.createTeamsIfNotExist(teamKeys);
        }

        User user = service.createOrUpdateUser(externalId, name, email);

        UserPrincipal userPrincipal = mapper.toUserPrincipal(user, admin, managerOf, viewerOf, deployerOf);

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                userPrincipal,
                authentication.getCredentials(),
                authentication.getAuthorities()
            )
        );

        filterChain.doFilter(request, response);
    }
}
