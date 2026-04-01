package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.error.ApiException;
import com.orchealm.orchealmapi.model.error.ErrorReference;
import com.orchealm.orchealmapi.model.project.ProjectMapper;
import com.orchealm.orchealmapi.model.project.ProjectRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.interceptor.RlsInterceptor;
import com.orchealm.orchealmapi.persistence.model.Project;
import com.orchealm.orchealmapi.persistence.model.ProjectView;
import com.orchealm.orchealmapi.persistence.model.ProjectViewQueryFilterDto;
import com.orchealm.orchealmapi.persistence.repository.ProjectRepository;
import com.orchealm.orchealmapi.persistence.repository.ProjectViewRepository;
import io.github.zorin95670.specification.SpringQueryFilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final RlsInterceptor rlsInterceptor;
    private final ProjectRepository repository;
    private final ProjectViewRepository projectViewRepository;
    private final ProjectMapper mapper;

    @Override
    public Project createProject(final UserPrincipal userPrincipal, final ProjectRecord projectRecord) {
        rlsInterceptor.securize(userPrincipal);

        return repository.save(mapper.toProject(userPrincipal, projectRecord, projectRecord.parent() != null));
    }

    @Override
    public Page<ProjectView> findProjectsView(final UserPrincipal userPrincipal,
                                              final ProjectViewQueryFilterDto filters,
                                              final Pageable pageable) {
        rlsInterceptor.securize(userPrincipal);

        return projectViewRepository.findAll(
            new SpringQueryFilterSpecification<>(ProjectView.class, filters),
            pageable
        );
    }

    @Override
    public Project findProjectById(final UserPrincipal userPrincipal, final UUID id) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("id", id.toString())));
    }

    @Override
    public ProjectView findProjectViewById(final UserPrincipal userPrincipal, final UUID id) {
        rlsInterceptor.securize(userPrincipal);

        return projectViewRepository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("id", id.toString())));
    }

    @Override
    public Project updateProject(final UserPrincipal userPrincipal,
                                 final UUID id,
                                 final ProjectRecord projectRecord) {
        var project = findProjectById(userPrincipal, id);

        project.setOrganization(projectRecord.organization());
        project.setName(projectRecord.name());
        project.setColor(projectRecord.color());
        project.setTextColor(projectRecord.textColor());
        project.setUpdatedBy(userPrincipal.getExternalId());

        return repository.save(project);
    }

    @Override
    public void deleteProject(final UserPrincipal userPrincipal, final UUID id) {
        var project = findProjectById(userPrincipal, id);

        repository.delete(project);
    }

    @Override
    public Project findProjectByKey(final UserPrincipal userPrincipal,
                                    final String organization,
                                    final String name) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findProjectByOrganizationAndName(organization, name)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound(
                "key", String.format("%s.%s", organization, name)))
            );
    }
}
