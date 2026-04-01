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
import org.springframework.transaction.annotation.Transactional;
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
        userPrincipal.assertCanManage(projectRecord.teamKey());
        assertValidParent(null, projectRecord.teamKey(), projectRecord.parent());

        return repository.save(mapper.toProject(userPrincipal, projectRecord));
    }

    /**
     * Checks the parent of a project. Projects have a single level of hierarchy: a master project (no parent)
     * and its sub-projects. The parent must therefore:
     * <ul>
     *     <li>exist and belong to the same team (also enforced by {@code fk_projects_parent}, checked here to
     *     answer 400 instead of 500);</li>
     *     <li>be a master project, and not the project itself;</li>
     * </ul>
     * and a project that has sub-projects cannot get a parent.
     *
     * @param projectId the project being updated, null on creation
     * @param teamKey   the team of the project
     * @param parentId  the parent project id, may be null
     */
    private void assertValidParent(final UUID projectId, final String teamKey, final UUID parentId) {
        if (parentId == null) {
            return;
        }

        boolean valid = !parentId.equals(projectId)
            && repository.findById(parentId)
                .map(parent -> parent.getTeamKey().equals(teamKey) && parent.getParent() == null)
                .orElse(false)
            && (projectId == null || !repository.existsByParent(projectId));

        if (!valid) {
            throw new ApiException(ErrorReference.invalidProjectParent());
        }
    }

    @Override
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
    public Project findProjectById(final UserPrincipal userPrincipal, final UUID id) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findById(id)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound("id", id.toString())));
    }

    @Override
    @Transactional(readOnly = true)
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

        userPrincipal.assertCanManage(project.getTeamKey());

        if (!project.matchesRowVersion(projectRecord.rowVersion())) {
            throw new ApiException(ErrorReference.conflict());
        }

        project.setOrganization(projectRecord.organization());
        project.setName(projectRecord.name());
        project.setColor(projectRecord.color());
        project.setTextColor(projectRecord.textColor());
        assertValidParent(project.getId(), project.getTeamKey(), projectRecord.parent());
        project.setParent(projectRecord.parent());
        project.setUpdatedBy(userPrincipal.getExternalId());

        return repository.save(project);
    }

    @Override
    public void deleteProject(final UserPrincipal userPrincipal, final UUID id) {
        var project = findProjectById(userPrincipal, id);

        userPrincipal.assertCanManage(project.getTeamKey());

        repository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public Project findProjectByKey(final UserPrincipal userPrincipal,
                                    final String teamKey,
                                    final String organization,
                                    final String name) {
        rlsInterceptor.securize(userPrincipal);

        return repository.findByTeamKeyAndOrganizationAndName(teamKey, organization, name)
            .orElseThrow(() -> new ApiException(ErrorReference.entityNotFound(
                "key", String.format("%s.%s.%s", teamKey, organization, name)))
            );
    }
}
