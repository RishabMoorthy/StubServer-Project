package org.common.db.repository;

import org.common.db.entity.VsExecutionMode;

import java.util.List;
import java.util.Optional;

public class VsExecutionModeRepository extends AbstractRepository {

    public List<VsExecutionMode> findByMasterId(Long masterId) {
        return executeInSession(session ->
                session.createQuery(
                        "FROM VsExecutionMode m WHERE m.masterId = :masterId",
                        VsExecutionMode.class)
                        .setParameter("masterId", masterId)
                        .getResultList()
        );
    }

    // ===== Java-Backend support =====

    public Optional<VsExecutionMode> findByMasterIdAndVirtServer(Long masterId, String virtServer) {
        return executeReadOnly(session ->
                session.createQuery(
                        "FROM VsExecutionMode m WHERE m.masterId = :masterId AND m.virtServer = :vs",
                        VsExecutionMode.class)
                        .setParameter("masterId", masterId)
                        .setParameter("vs", virtServer)
                        .setMaxResults(1)
                        .uniqueResultOptional()
        );
    }

    public VsExecutionMode save(VsExecutionMode entity) {
        return executeInSession(session -> (VsExecutionMode) session.merge(entity));
    }
}
