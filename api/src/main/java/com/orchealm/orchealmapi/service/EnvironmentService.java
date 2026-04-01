package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.environment.EnvironmentRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.Environment;
import com.orchealm.orchealmapi.persistence.model.EnvironmentQueryFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EnvironmentService {
    Environment createEnvironment(UserPrincipal userPrincipal, EnvironmentRecord environmentRecord);

    Page<Environment> findEnvironments(UserPrincipal userPrincipal, EnvironmentQueryFilterDto filters,
                                       Pageable pageable);

    Environment findEnvironmentById(UserPrincipal userPrincipal, UUID id);

    Environment findEnvironmentByName(UserPrincipal userPrincipal, String name);

    Environment findEnvironmentByShortName(UserPrincipal userPrincipal, String shortName);

    Environment updateEnvironment(UserPrincipal userPrincipal, UUID id, EnvironmentRecord environmentRecord);

    void deleteEnvironment(UserPrincipal userPrincipal, UUID id);
}
