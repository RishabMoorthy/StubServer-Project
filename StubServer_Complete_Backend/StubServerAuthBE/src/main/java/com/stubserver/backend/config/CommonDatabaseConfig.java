package com.stubserver.backend.config;

import org.common.db.DataSourceManager;
import org.common.db.HibernateUtil;
import org.common.db.repository.AssignedServiceRepository;
import org.common.db.repository.AuditLogRepository;
import org.common.db.repository.MasterCatalogRepository;
import org.common.db.repository.MetricsRepository;
import org.common.db.repository.PortRangeRepository;
import org.common.db.repository.RefreshTokenRepository;
import org.common.db.repository.UserRepository;
import org.common.db.repository.VsCatalogRepository;
import org.common.db.repository.VsExecutionModeRepository;
import org.common.db.repository.VsLiveUrlRepository;
import org.common.db.repository.VsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Wires the common Hibernate framework (org.common.db) into the Spring context.
 *
 * Java-Backend no longer owns a DataSource, EntityManagerFactory or any
 * repository of its own. Database connectivity, entities and repositories all
 * live in the common project and are consumed here exactly the way
 * StubServerCore consumes them — through plain Hibernate.
 *
 * The common framework reads its configuration (DB url/user/password and table
 * names) from config_*.properties on the classpath of the common jar. Select the
 * environment file with -Dconfig.name=config_uat.properties (defaults to
 * config_local.properties).
 */
@Configuration
public class CommonDatabaseConfig {

    /** Initialise the shared pool + SessionFactory eagerly so startup fails fast. */
    @PostConstruct
    public void initCommonFramework() {
        DataSourceManager.getInstance();
        HibernateUtil.getSessionFactory();
    }

    // ===== Repository beans (plain Hibernate, from the common framework) =====

    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }

    @Bean
    public RefreshTokenRepository refreshTokenRepository() {
        return new RefreshTokenRepository();
    }

    @Bean
    public AuditLogRepository auditLogRepository() {
        return new AuditLogRepository();
    }

    @Bean
    public AssignedServiceRepository assignedServiceRepository() {
        return new AssignedServiceRepository();
    }

    @Bean
    public PortRangeRepository portRangeRepository() {
        return new PortRangeRepository();
    }

    @Bean
    public VsCatalogRepository vsCatalogRepository() {
        return new VsCatalogRepository();
    }

    @Bean
    public MasterCatalogRepository masterCatalogRepository() {
        return new MasterCatalogRepository();
    }

    @Bean
    public VsRepository vsRepository() {
        return new VsRepository();
    }

    @Bean
    public VsExecutionModeRepository vsExecutionModeRepository() {
        return new VsExecutionModeRepository();
    }

    @Bean
    public VsLiveUrlRepository vsLiveUrlRepository() {
        return new VsLiveUrlRepository();
    }

    @Bean
    public MetricsRepository metricsRepository() {
        return new MetricsRepository();
    }
}
