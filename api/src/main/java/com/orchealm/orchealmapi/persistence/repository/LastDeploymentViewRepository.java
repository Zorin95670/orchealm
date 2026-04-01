package com.orchealm.orchealmapi.persistence.repository;

import com.orchealm.orchealmapi.persistence.model.LastDeploymentView;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface LastDeploymentViewRepository
    extends Repository<LastDeploymentView, UUID>, JpaSpecificationExecutor<LastDeploymentView> {
    /**
     * Retrieves a paginated list of {@link LastDeploymentView} entities matching the given specification.
     *
     * <p>
     * This method allows dynamic filtering based on the provided {@link Specification},
     * typically generated from query parameters (e.g. via {@code @QueryFilter}).
     * </p>
     *
     * <p>
     * Results are returned as a {@link Page}, enabling efficient pagination and sorting.
     * </p>
     *
     * @param specification the filtering criteria to apply (can be {@code null} to fetch all records)
     * @param pageable      the pagination and sorting configuration (must not be {@code null})
     * @return a page of {@link LastDeploymentView} matching the given specification
     */
    @Override
    @NonNull
    Page<LastDeploymentView> findAll(@NonNull Specification<LastDeploymentView> specification,
                                     @NonNull Pageable pageable);
}
