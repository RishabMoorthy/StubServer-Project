package org.common.db.repository;

import org.common.db.entity.VsCatalog;

import java.util.List;
import java.util.Optional;

/**
 * Plain-Hibernate repository for the VS catalog (READYAPI_VS_CATALOG).
 */
public class VsCatalogRepository extends AbstractRepository {

    public List<VsCatalog> findAll() {
        return executeReadOnly(session ->
                session.createQuery("FROM VsCatalog", VsCatalog.class).list()
        );
    }

    public Optional<VsCatalog> findByVsname(String vsName) {
        return executeReadOnly(session ->
                session.createQuery("FROM VsCatalog v WHERE v.vsName = :n", VsCatalog.class)
                        .setParameter("n", vsName)
                        .setMaxResults(1)
                        .uniqueResultOptional()
        );
    }
}
