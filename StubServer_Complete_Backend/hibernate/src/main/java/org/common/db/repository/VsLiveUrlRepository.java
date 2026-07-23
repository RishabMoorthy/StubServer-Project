package org.common.db.repository;

import org.common.db.entity.VsLiveUrl;

import java.util.List;
import java.util.Optional;

public class VsLiveUrlRepository extends AbstractRepository {

    public Optional<VsLiveUrl> findActiveByVsId(Long vsId) {
        return executeInSession(session ->
                session.createQuery(
                        "FROM VsLiveUrl v WHERE v.vsid = :vsId AND v.isActive = 'Y'",
                        VsLiveUrl.class)
                        .setParameter("vsId", vsId)
                        .uniqueResultOptional()
        );
    }

    // ===== Java-Backend support =====

    public List<VsLiveUrl> findByVsid(Long vsid) {
        return executeReadOnly(session ->
                session.createQuery("FROM VsLiveUrl v WHERE v.vsid = :vsid", VsLiveUrl.class)
                        .setParameter("vsid", vsid)
                        .list()
        );
    }

    public boolean existsByVsidAndHost(Long vsid, String host) {
        Long count = executeReadOnly(session ->
                session.createQuery(
                        "SELECT COUNT(v) FROM VsLiveUrl v WHERE v.vsid = :vsid AND v.host = :host",
                        Long.class)
                        .setParameter("vsid", vsid)
                        .setParameter("host", host)
                        .uniqueResult()
        );
        return count != null && count > 0;
    }

    public Optional<VsLiveUrl> findTopByOrderByVsUrlIdDesc() {
        return executeReadOnly(session ->
                session.createQuery("FROM VsLiveUrl v ORDER BY v.vsUrlId DESC", VsLiveUrl.class)
                        .setMaxResults(1)
                        .uniqueResultOptional()
        );
    }

    public Optional<VsLiveUrl> findById(Long vsUrlId) {
        return executeReadOnly(session ->
                Optional.ofNullable(session.get(VsLiveUrl.class, vsUrlId))
        );
    }

    public VsLiveUrl save(VsLiveUrl entity) {
        return executeInSession(session -> (VsLiveUrl) session.merge(entity));
    }

    public void saveAll(List<VsLiveUrl> entities) {
        executeInSession(session -> {
            for (VsLiveUrl e : entities) {
                session.merge(e);
            }
            return null;
        });
    }

    public void deleteById(Long vsUrlId) {
        executeInSession(session -> {
            session.createMutationQuery("DELETE FROM VsLiveUrl v WHERE v.vsUrlId = :id")
                    .setParameter("id", vsUrlId)
                    .executeUpdate();
            return null;
        });
    }
}
