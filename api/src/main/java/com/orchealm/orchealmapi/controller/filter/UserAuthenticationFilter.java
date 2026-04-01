package com.orchealm.orchealmapi.controller.filter;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.model.user.UserMapper;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.User;
import com.orchealm.orchealmapi.service.TeamService;
import com.orchealm.orchealmapi.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final UserService service;
    private final UserMapper mapper;
    private final TeamService teamService;
    private final TaskExecutor teamTaskExecutor;
    private final String externalIdClaim;
    private final String nameClaim;
    private final String emailClaim;

    public static String convertTeams(final List<String> teams) {
        if (teams == null || teams.isEmpty()) {
            return "{}";
        }

        Map<String, List<String>> result = new HashMap<>();

        teams.stream()
            .map(team -> team.split(":"))
            .forEach(parts -> result.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]));

        return OBJECT_MAPPER.writeValueAsString(result);
    }

    public static String convertGlobalRoles(final List<String> globalRoles) {
        if (globalRoles == null || globalRoles.isEmpty()) {
            return "[]";
        }

        return OBJECT_MAPPER.writeValueAsString(globalRoles);
    }

    @Override
    protected void doFilterInternal(final @NonNull HttpServletRequest request,
                                    final @NonNull HttpServletResponse response,
                                    final @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            System.out.println("passe");
            throw new ApiException(ErrorReference.notFound());
        }

        String externalId = jwt.getClaimAsString(externalIdClaim);
        String name = jwt.getClaimAsString(nameClaim);
        String email = jwt.getClaimAsString(emailClaim);
        List<String> globalRoles = jwt.getClaim("global_roles");
        List<String> teams = jwt.getClaim("teams");

        System.out.println(name);
        System.out.println(email);
        System.out.println(globalRoles);
        System.out.println(teams);
        createTeams(teams);

        User user = service.createOrUpdateUser(externalId, name, email);

        UserPrincipal userPrincipal = mapper.toUserPrincipal(
            user,
            convertGlobalRoles(globalRoles),
            convertTeams(teams)
        );


        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(
                userPrincipal,
                authentication.getCredentials(),
                authentication.getAuthorities()
            )
        );

        filterChain.doFilter(request, response);
    }

    public void createTeams(final List<String> teams) {
        if (teams == null || teams.isEmpty()) {
            return;
        }
        Set<String> teamKeys = teams.stream()
            .map(t -> t.split(":")[0])
            .collect(Collectors.toSet());

        teamTaskExecutor.execute(() -> {
            try {
                teamKeys.forEach(teamService::createTeamIfNotExist);
            } catch (Exception e) {
                e.printStackTrace(); // log
            }
        });

    }
}
