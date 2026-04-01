package com.orchealm.orchealmapi.service;

import com.orchealm.orchealmapi.model.deployment.DeploymentRecord;
import com.orchealm.orchealmapi.model.deployment.PlannedDeploymentRecord;
import com.orchealm.orchealmapi.model.user.UserPrincipal;
import com.orchealm.orchealmapi.persistence.model.Deployment;
import com.orchealm.orchealmapi.persistence.model.DeploymentView;
import com.orchealm.orchealmapi.persistence.model.DeploymentViewQueryFilterDto;
import com.orchealm.orchealmapi.persistence.model.LastDeploymentView;
import com.orchealm.orchealmapi.persistence.model.LastDeploymentViewQueryFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


/**
 * Service interface for managing deployments.
 * Provides operations to create, read, update, and delete deployments, while enforcing
 * RLS (Row-Level Security) permissions based on the provided {@link UserPrincipal}.
 */
public interface DeploymentService {
    /**
     * Creates a new deployment.
     *
     * @param userPrincipal    the authenticated user performing the operation
     * @param deploymentRecord the data for the deployment to create
     * @return the newly created {@link Deployment} entity
     */
    Deployment planDeployment(UserPrincipal userPrincipal, PlannedDeploymentRecord deploymentRecord);

    Deployment startDeployment(UserPrincipal userPrincipal, DeploymentRecord deploymentRecord);

    Deployment stopDeployment(UserPrincipal userPrincipal, DeploymentRecord deploymentRecord);

    Page<LastDeploymentView> getLastDeployments(UserPrincipal userPrincipal,
                                                LastDeploymentViewQueryFilterDto filters,
                                                Pageable pageable);

    Page<DeploymentView> getDeployments(UserPrincipal userPrincipal,
                                        DeploymentViewQueryFilterDto filters,
                                        Pageable pageable);


}
