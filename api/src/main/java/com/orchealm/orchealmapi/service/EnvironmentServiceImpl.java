package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.environment.EnvironmentMapper;
import com.orchealm.orchealmapi.model.environment.EnvironmentRecord;
import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.interceptor.RlsInterceptor;
import com.orchealm.orchealmapi.persistence.model.Environment;
import com.orchealm.orchealmapi.persistence.model.EnvironmentQueryFilterDto;
import com.orchealm.orchealmapi.persistence.repository.EnvironmentRepository;
import io.github.zorin95670.specification.SpringQueryFilterSpecification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
public class EnvironmentServiceImpl implements EnvironmentService {

    private final RlsInterceptor rlsInterceptor;
    private final EnvironmentRepository repository;
    private final EnvironmentMapper mapper;

    @Autowired
    public EnvironmentServiceImpl(final RlsInterceptor rlsInterceptor,
                                  final EnvironmentRepository repository,
                                  final EnvironmentMapper mapper) {
        this.rlsInterceptor = rlsInterceptor;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Environment createEnvironment(final UserPrincipal userPrincipal, final EnvironmentRecord environmentRecord) {
        rlsInterceptor.securize(userPrincipal);

        return repository.save(mapper.toEnvironment(userPrincipal, environmentRecord));
    }

    @Override
    public Page<Environment> findEnvironments(final UserPrincipal userPrincipal,
                                              final EnvironmentQueryFilterDto filters,
                                              final Pageable pageable) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findAll(
            new SpringQueryFilterSpecification<>(Environment.class, filters),
            pageable
        );
    }

    @Override
    public Environment findEnvironmentById(final UserPrincipal userPrincipal, final UUID id) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("id", id.toString())));
    }

    @Override
    public Environment findEnvironmentByName(final UserPrincipal userPrincipal, final String name) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findByName(name)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("name", name)));
    }

    @Override
    public Environment findEnvironmentByShortName(final UserPrincipal userPrincipal, final String shortName) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findByShortName(shortName)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("shortName", shortName)));
    }

    @Override
    public Environment updateEnvironment(final UserPrincipal userPrincipal,
                                         final UUID id,
                                         final EnvironmentRecord environmentRecord) {
        var environment = findEnvironmentById(userPrincipal, id);

        environment.setName(environmentRecord.name());
        environment.setShortName(environmentRecord.shortName());
        environment.setPosition(environmentRecord.position());
        environment.setUpdatedBy(userPrincipal.getExternalId());

        return repository.save(environment);
    }

    @Override
    public void deleteEnvironment(final UserPrincipal userPrincipal, final UUID id) {
        var environment = findEnvironmentById(userPrincipal, id);

        repository.delete(environment);
    }
}
