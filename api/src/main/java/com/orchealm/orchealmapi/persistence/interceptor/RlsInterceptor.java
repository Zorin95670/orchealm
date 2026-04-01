package com.orchealm.orchealmapi.persistence.interceptor;

import com.orchealm.orchealmapi.model.user.UserPrincipal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * RlsInterceptor is responsible for setting PostgreSQL Row-Level Security (RLS) variables
 * for the current transaction based on the authenticated user.
 *
 * <p>This interceptor sets RLS context variables such as 'app.roles' and 'app.teams' to ensure
 * that all subsequent database operations within the same Spring-managed transaction
 * are executed with the correct permission context.
 *
 * <p>It must be called within an active transaction. Calling it outside of a transaction
 * will result in an {@link IllegalStateException}.
 */
@Component
public class RlsInterceptor {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Sets the RLS context variables for the current transaction.
     *
     * <p>This includes:
     * <ul>
     *     <li>{@code app.roles} – the global roles of the current user</li>
     *     <li>{@code app.teams} – the team-specific roles of the current user</li>
     * </ul>
     *
     * @param userPrincipal the authenticated user containing roles and team permissions
     * @throws IllegalStateException if called outside of an active transaction
     */
    public void securize(final UserPrincipal userPrincipal) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException(
                "No active transaction: RLS variable can only be set inside a transaction"
            );
        }

        entityManager.createNativeQuery("SET LOCAL \"app.roles\" = '" + userPrincipal.getGlobalRoles() + "'")
            .executeUpdate();

        entityManager.createNativeQuery("SET LOCAL \"app.teams\" = '" + userPrincipal.getTeams() + "'")
            .executeUpdate();
    }

    public void securizeWithAdmin() {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException(
                "No active transaction: RLS variable can only be set inside a transaction"
            );
        }

        entityManager.createNativeQuery("SET LOCAL \"app.roles\" = '[\"ADMIN\"]'")
            .executeUpdate();

        entityManager.createNativeQuery("SET LOCAL \"app.teams\" = '{}'")
            .executeUpdate();
    }
}
