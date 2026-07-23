package org.common.db.repository;

import org.common.db.entity.AuditLog;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Plain-Hibernate repository for audit log entries.
 */
public class AuditLogRepository extends AbstractRepository {

    /** All audit logs, most recent first (replaces Spring Data findAll(Sort)). */
    public List<AuditLog> findAllOrderByTimestampDesc() {
        return executeReadOnly(session ->
                session.createQuery("FROM AuditLog a ORDER BY a.timestamp DESC", AuditLog.class)
                        .list()
        );
    }

    public Optional<AuditLog> findFirstByActionTypeInOrderByTimestampDesc(Collection<String> actionTypes) {
        return executeReadOnly(session ->
                session.createQuery(
                        "FROM AuditLog a WHERE a.actionType IN (:types) ORDER BY a.timestamp DESC",
                        AuditLog.class)
                        .setParameterList("types", actionTypes)
                        .setMaxResults(1)
                        .uniqueResultOptional()
        );
    }

    public AuditLog save(AuditLog entity) {
        return executeInSession(session -> {
            session.persist(entity);
            return entity;
        });
    }
}
