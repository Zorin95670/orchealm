package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.deployment.DeploymentMapper;
import com.orchealm.orchealmapi.model.deployment.DeploymentRecord;
import com.orchealm.orchealmapi.model.deployment.PlannedDeploymentRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.attribute.TstzRange;
import com.orchealm.orchealmapi.persistence.interceptor.RlsInterceptor;
import com.orchealm.orchealmapi.persistence.model.Deployment;
import com.orchealm.orchealmapi.persistence.model.DeploymentQueryFilterDto;
import com.orchealm.orchealmapi.persistence.model.DeploymentView;
import com.orchealm.orchealmapi.persistence.model.DeploymentViewQueryFilterDto;
import com.orchealm.orchealmapi.persistence.model.LastDeploymentView;
import com.orchealm.orchealmapi.persistence.model.LastDeploymentViewQueryFilterDto;
import com.orchealm.orchealmapi.persistence.repository.DeploymentRepository;
import com.orchealm.orchealmapi.persistence.repository.DeploymentViewRepository;
import com.orchealm.orchealmapi.persistence.repository.LastDeploymentViewRepository;
import io.github.zorin95670.specification.SpringQueryFilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeploymentServiceImpl implements DeploymentService {

    private final RlsInterceptor rlsInterceptor;

    private final DeploymentRepository repository;
    private final LastDeploymentViewRepository lastDeploymentViewRepository;
    private final DeploymentViewRepository deploymentViewRepository;

    private final DeploymentMapper mapper;

    private final EnvironmentService environmentService;
    private final ProjectService projectService;

    @Override
    public Deployment planDeployment(final UserPrincipal userPrincipal,
                                     final PlannedDeploymentRecord deploymentRecord) {
        rlsInterceptor.securize(userPrincipal);

        var environment = environmentService.findEnvironmentByShortName(userPrincipal, deploymentRecord.environment());
        var project = projectService.findProjectByKey(
            userPrincipal,
            deploymentRecord.projectOrganization(),
            deploymentRecord.projectName()
        );

        var deployment = mapper.toDeployment(userPrincipal, deploymentRecord);

        deployment.setEnvironmentId(environment.getId());
        deployment.setProjectId(project.getId());
        deployment.setStatus(Deployment.DeploymentStatus.PLANNED.name());

        return repository.save(deployment);
    }

    @Override
    public Deployment startDeployment(final UserPrincipal userPrincipal, final DeploymentRecord deploymentRecord) {
        rlsInterceptor.securize(userPrincipal);

        var environment = environmentService.findEnvironmentByShortName(userPrincipal, deploymentRecord.environment());
        var project = projectService.findProjectByKey(
            userPrincipal,
            deploymentRecord.projectOrganization(),
            deploymentRecord.projectName()
        );

        var filter = new DeploymentQueryFilterDto();
        filter.setTeamKey(List.of(deploymentRecord.teamKey()));
        filter.setProjectId(List.of(project.getId().toString()));
        filter.setEnvironmentId(List.of(environment.getId().toString()));
        filter.setVersion(List.of(deploymentRecord.version()));
        filter.setClient(List.of(deploymentRecord.client()));
        filter.setStatus(List.of(Deployment.DeploymentStatus.PLANNED.name()));

        var deploymentOpt = repository.findOne(new SpringQueryFilterSpecification<>(Deployment.class, filter));

        Deployment deployment;

        if (deploymentOpt.isPresent()) {
            deployment = deploymentOpt.get();
        } else {
            deployment = new Deployment();
            deployment.setTeamKey(deploymentRecord.teamKey());
            deployment.setProjectId(project.getId());
            deployment.setEnvironmentId(environment.getId());
            deployment.setVersion(deploymentRecord.version());
            deployment.setClient(deploymentRecord.client());
        }

        deployment.setStatus(Deployment.DeploymentStatus.IN_PROGRESS.name());
        deployment.setActualPeriod(new TstzRange(OffsetDateTime.now(), null));
        deployment.setCreatedBy(userPrincipal.getName());
        deployment.setUpdatedBy(userPrincipal.getName());

        return repository.save(deployment);
    }

    @Override
    public Deployment stopDeployment(final UserPrincipal userPrincipal, final DeploymentRecord deploymentRecord) {
        rlsInterceptor.securize(userPrincipal);

        var environment = environmentService.findEnvironmentByShortName(userPrincipal, deploymentRecord.environment());
        var project = projectService.findProjectByKey(
            userPrincipal,
            deploymentRecord.projectOrganization(),
            deploymentRecord.projectName()
        );

        var filter = new DeploymentQueryFilterDto();
        filter.setTeamKey(List.of(deploymentRecord.teamKey()));
        filter.setProjectId(List.of(project.getId().toString()));
        filter.setEnvironmentId(List.of(environment.getId().toString()));
        filter.setVersion(List.of(deploymentRecord.version()));
        filter.setClient(List.of(deploymentRecord.client()));
        filter.setStatus(List.of(Deployment.DeploymentStatus.IN_PROGRESS.name()));

        var deploymentOpt = repository.findOne(new SpringQueryFilterSpecification<>(Deployment.class, filter));

        Deployment deployment;

        if (deploymentOpt.isPresent()) {
            deployment = deploymentOpt.get();
        } else {
            deployment = new Deployment();
            deployment.setTeamKey(deploymentRecord.teamKey());
            deployment.setProjectId(project.getId());
            deployment.setEnvironmentId(environment.getId());
            deployment.setVersion(deploymentRecord.version());
            deployment.setClient(deploymentRecord.client());
            deployment.setActualPeriod(new TstzRange(OffsetDateTime.now(), null));
        }

        if (Deployment.DeploymentStatus.FAILED.name().equalsIgnoreCase(deploymentRecord.status())) {
            deployment.setStatus(Deployment.DeploymentStatus.FAILED.name());
        } else {
            deployment.setStatus(Deployment.DeploymentStatus.COMPLETED.name());
        }

        deployment.getActualPeriod().setEnd(OffsetDateTime.now());
        deployment.setCreatedBy(userPrincipal.getName());
        deployment.setUpdatedBy(userPrincipal.getName());

        return repository.save(deployment);
    }

    @Override
    public Page<LastDeploymentView> getLastDeployments(final UserPrincipal userPrincipal,
                                                       final LastDeploymentViewQueryFilterDto filters,
                                                       final Pageable pageable) {
        rlsInterceptor.securize(userPrincipal);

        return lastDeploymentViewRepository.findAll(
            new SpringQueryFilterSpecification<>(LastDeploymentView.class, filters),
            pageable
        );
    }

    @Override
    public Page<DeploymentView> getDeployments(final UserPrincipal userPrincipal,
                                               final DeploymentViewQueryFilterDto filters,
                                               final Pageable pageable) {
        rlsInterceptor.securize(userPrincipal);

        return deploymentViewRepository.findAll(
            new SpringQueryFilterSpecification<>(DeploymentView.class, filters),
            pageable
        );
    }
}
