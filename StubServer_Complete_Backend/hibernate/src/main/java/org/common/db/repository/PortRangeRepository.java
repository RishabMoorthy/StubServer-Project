package org.common.db.repository;

import org.common.db.entity.PortRange;

import java.util.List;
import java.util.Optional;

/**
 * Plain-Hibernate repository for application/port ranges (READYAPI_PORT_RANGE).
 */
public class PortRangeRepository extends AbstractRepository {

    public List<PortRange> findAll() {
        return executeReadOnly(session ->
                session.createQuery("FROM PortRange", PortRange.class).list()
        );
    }

    public Optional<PortRange> findById(Long portId) {
        return executeReadOnly(session ->
                Optional.ofNullable(session.get(PortRange.class, portId))
        );
    }

    public boolean existsById(Long portId) {
        return findById(portId).isPresent();
    }

    public boolean existsByAppName(String appName) {
        Long count = executeReadOnly(session ->
                session.createQuery(
                        "SELECT COUNT(p) FROM PortRange p WHERE p.appName = :a", Long.class)
                        .setParameter("a", appName)
                        .uniqueResult()
        );
        return count != null && count > 0;
    }

    public Optional<PortRange> findTopByOrderByPortIdDesc() {
        return executeReadOnly(session ->
                session.createQuery("FROM PortRange p ORDER BY p.portId DESC", PortRange.class)
                        .setMaxResults(1)
                        .uniqueResultOptional()
        );
    }

    public PortRange save(PortRange entity) {
        return executeInSession(session -> (PortRange) session.merge(entity));
    }

    public void deleteById(Long portId) {
        executeInSession(session -> {
            session.createMutationQuery("DELETE FROM PortRange p WHERE p.portId = :id")
                    .setParameter("id", portId)
                    .executeUpdate();
            return null;
        });
    }
}
