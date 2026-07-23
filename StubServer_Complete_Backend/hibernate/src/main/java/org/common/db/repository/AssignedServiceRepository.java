package org.common.db.repository;

import org.common.db.entity.AssignedService;

import java.util.List;

/**
 * Plain-Hibernate repository for user → service assignments.
 */
public class AssignedServiceRepository extends AbstractRepository {

    public List<AssignedService> findByIdUsername(String username) {
        return executeReadOnly(session ->
                session.createQuery(
                        "FROM AssignedService a WHERE a.id.username = :u",
                        AssignedService.class)
                        .setParameter("u", username)
                        .list()
        );
    }

    public void deleteByIdUsername(String username) {
        executeInSession(session -> {
            session.createMutationQuery("DELETE FROM AssignedService a WHERE a.id.username = :u")
                    .setParameter("u", username)
                    .executeUpdate();
            return null;
        });
    }

    public void saveAll(List<AssignedService> entries) {
        executeInSession(session -> {
            for (AssignedService entry : entries) {
                session.merge(entry);
            }
            return null;
        });
    }
}
