package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.interceptor.RlsInterceptor;
import com.orchealm.orchealmapi.persistence.model.Deployment;
import com.orchealm.orchealmapi.persistence.model.DeploymentQueryFilterDto;
import io.github.zorin95670.executor.SpringQueryExecutor;
import io.github.zorin95670.specification.SpringQueryFilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final RlsInterceptor rlsInterceptor;
    private final SpringQueryExecutor executor;

    @Override
    public Page<String> findClient(final UserPrincipal userPrincipal,
                                   final DeploymentQueryFilterDto filters,
                                   final Pageable pageable) {
        rlsInterceptor.securize(userPrincipal);

        return executor.findDistinctPage(
            Deployment.class,
            String.class,
            new SpringQueryFilterSpecification<>(Deployment.class, filters),
            pageable,
            "client"
        );
    }
}
