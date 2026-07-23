package org.common.db.repository;

import org.common.db.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * Plain-Hibernate repository for portal users (STUBSERVERUSERS).
 * Mirrors the methods the Java-Backend services depend on.
 */
public class UserRepository extends AbstractRepository {

    public Optional<User> findByUsername(String username) {
        return executeReadOnly(session ->
                session.createQuery("FROM User u WHERE u.username = :u", User.class)
                        .setParameter("u", username)
                        .uniqueResultOptional()
        );
    }

    public boolean existsByUsernameOrEmail(String username, String email) {
        Long count = executeReadOnly(session ->
                session.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :u OR u.email = :e",
                        Long.class)
                        .setParameter("u", username)
                        .setParameter("e", email)
                        .uniqueResult()
        );
        return count != null && count > 0;
    }

    public boolean existsById(String username) {
        return findByUsername(username).isPresent();
    }

    public List<User> findAll() {
        return executeReadOnly(session ->
                session.createQuery("FROM User", User.class).list()
        );
    }

    public User save(User entity) {
        return executeInSession(session -> (User) session.merge(entity));
    }

    public void deleteById(String username) {
        executeInSession(session -> {
            session.createMutationQuery("DELETE FROM User u WHERE u.username = :u")
                    .setParameter("u", username)
                    .executeUpdate();
            return null;
        });
    }
}
