package com.orchealm.orchealmapi.model.deployment;

import com.orchealm.orchealmapi.model.common.CommonMapper;
import com.orchealm.orchealmapi.persistence.model.Deployment;
import com.orchealm.orchealmapi.persistence.model.DeploymentView;
import com.orchealm.orchealmapi.persistence.model.LastDeploymentView;
import com.orchealm.orchealmapi.persistence.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CommonMapper.class)
public interface DeploymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "environmentId", ignore = true)
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "actualPeriod", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "teamKey", source = "plannedDeploymentRecord.teamKey")
    @Mapping(target = "version", source = "plannedDeploymentRecord.version")
    @Mapping(target = "client", source = "plannedDeploymentRecord.client")
    @Mapping(target = "plannedPeriod", source = "plannedDeploymentRecord.plannedPeriod", qualifiedByName = "toRange")
    @Mapping(target = "createdBy", source = "user.externalId")
    @Mapping(target = "updatedBy", source = "user.externalId")
    @Mapping(target = "insertDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Deployment toDeployment(User user, PlannedDeploymentRecord plannedDeploymentRecord);

    @Mapping(target = "plannedPeriod", source = "plannedPeriod", qualifiedByName = "toPeriodDTO")
    @Mapping(target = "actualPeriod", source = "actualPeriod", qualifiedByName = "toPeriodDTO")
    DeploymentDTO toDto(Deployment deployment);

    @Mapping(target = "plannedPeriod", source = "plannedPeriod", qualifiedByName = "toPeriodDTO")
    @Mapping(target = "actualPeriod", source = "actualPeriod", qualifiedByName = "toPeriodDTO")
    @Mapping(target = "state", source = "state")
    DeploymentViewDTO toDto(LastDeploymentView deployment);

    @Mapping(target = "plannedPeriod", source = "plannedPeriod", qualifiedByName = "toPeriodDTO")
    @Mapping(target = "actualPeriod", source = "actualPeriod", qualifiedByName = "toPeriodDTO")
    @Mapping(target = "state", source = "state")
    @Mapping(target = "plannedPeriodStart", source = "plannedPeriodStart")
    @Mapping(target = "plannedPeriodEnd", source = "plannedPeriodEnd")
    @Mapping(target = "actualPeriodStart", source = "actualPeriodStart")
    @Mapping(target = "actualPeriodEnd", source = "actualPeriodEnd")
    DeploymentViewDTO toDto(DeploymentView deployment);
}
