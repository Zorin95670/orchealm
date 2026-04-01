package com.orchealm.orchealmapi.persistence.interceptor;

import com.orchealm.orchealmapi.model.user.UserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * RlsInterceptor is responsible for setting PostgreSQL Row-Level Security (RLS) variables
 * for the current transaction based on the authenticated user.
 *
 * <p>It sets {@code app.is_admin}, {@code app.manager_of}, {@code app.viewer_of} and {@code app.deployer_of},
 * read by the RLS policies through the {@code app_is_admin()} and {@code app_in()} SQL functions.
 * Values are bound as query parameters, never concatenated into the SQL.
 *
 * <p>It must be called within an active transaction. Calling it outside of a transaction
 * will result in an {@link IllegalStateException}.
 */
@Component
public class RlsInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String SET_CONTEXT_QUERY = """
        SELECT set_config('app.is_admin', :isAdmin, true),
               set_config('app.manager_of', :managerOf, true),
               set_config('app.viewer_of', :viewerOf, true),
               set_config('app.deployer_of', :deployerOf, true)
        """;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sets the RLS context variables of the given user for the current transaction.
     *
     * @param userPrincipal the authenticated user containing roles and team permissions
     * @throws IllegalStateException if called outside of an active transaction
     */
    public void securize(final UserPrincipal userPrincipal) {
        setContext(
            userPrincipal.isAdmin(),
            userPrincipal.getManagerOf(),
            userPrincipal.getViewerOf(),
            userPrincipal.getDeployerOf()
        );
    }

    /**
     * Sets an ADMIN RLS context for the current transaction, for technical operations not bound to a user.
     *
     * @throws IllegalStateException if called outside of an active transaction
     */
    public void securizeWithAdmin() {
        setContext(true, List.of(), List.of(), List.of());
    }

    private void setContext(final boolean isAdmin,
                            final List<String> managerOf,
                            final List<String> viewerOf,
                            final List<String> deployerOf) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException(
                "No active transaction: RLS variable can only be set inside a transaction"
            );
        }

        entityManager.createNativeQuery(SET_CONTEXT_QUERY)
            .setParameter("isAdmin", String.valueOf(isAdmin))
            .setParameter("managerOf", toJson(managerOf))
            .setParameter("viewerOf", toJson(viewerOf))
            .setParameter("deployerOf", toJson(deployerOf))
            .getResultList();
    }

    private static String toJson(final List<String> values) {
        return OBJECT_MAPPER.writeValueAsString(values == null ? List.of() : values);
    }
}
