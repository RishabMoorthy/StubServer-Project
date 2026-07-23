package org.common.db.repository;

import org.common.db.entity.RefreshToken;

import java.util.Optional;

/**
 * Plain-Hibernate repository for persisted refresh tokens (AUTH_REFRESH_TOKENS).
 */
public class RefreshTokenRepository extends AbstractRepository {

    public Optional<RefreshToken> findByJti(String jti) {
        return executeReadOnly(session ->
                session.createQuery("FROM RefreshToken t WHERE t.jti = :j", RefreshToken.class)
                        .setParameter("j", jti)
                        .uniqueResultOptional()
        );
    }

    public RefreshToken save(RefreshToken entity) {
        return executeInSession(session -> (RefreshToken) session.merge(entity));
    }

    public void deleteById(String jti) {
        executeInSession(session -> {
            session.createMutationQuery("DELETE FROM RefreshToken t WHERE t.jti = :j")
                    .setParameter("j", jti)
                    .executeUpdate();
            return null;
        });
    }

    public void deleteAllByUsername(String username) {
        executeInSession(session -> {
            session.createMutationQuery("DELETE FROM RefreshToken t WHERE t.username = :u")
                    .setParameter("u", username)
                    .executeUpdate();
            return null;
        });
    }
}
