package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.DeploymentQueryFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {

    Page<String> findClient(UserPrincipal userPrincipal, DeploymentQueryFilterDto filters, Pageable pageable);
}
