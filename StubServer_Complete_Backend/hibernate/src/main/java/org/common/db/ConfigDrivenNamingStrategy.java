package org.common.db;

import org.common.db.config.ConfigLoader;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class ConfigDrivenNamingStrategy implements PhysicalNamingStrategy {

    @Override
    public Identifier toPhysicalTableName(Identifier logicalName, JdbcEnvironment env) {

        if (logicalName == null) {
            return null;
        }

        String logicalText = logicalName.getText();
        String resolvedTableName = resolveTableName(logicalText);

        System.out.println("[NamingStrategy] "
                + logicalText
                + " -> "
                + resolvedTableName);

        return Identifier.toIdentifier(resolvedTableName, logicalName.isQuoted());
    }

    private String resolveTableName(String logicalName) {

        String appEnv = ConfigLoader.getProperty("app.env");

        if (appEnv == null || appEnv.isBlank()) {
            appEnv = "qa";
        }

        appEnv = appEnv.trim().toLowerCase();

        switch (logicalName) {

            // ================= StubServerCore / shared entities =================

            case "VSDetails":
            case "STUBSERVERQA_VSDETAILS":
            case "STUBSERVERUAT_VSDETAILS":
            case "STUBSERVER_VSDETAILS_TEST":
                return resolveVsDetailsTable(appEnv);

            case "MasterCatalog":
            case "STUBSERVER_MASTER_CATALOG_QA":
            case "STUBSERVER_MASTER_CATALOG_UAT":
            case "STUBSERVER MASTER CATALOG TEST":
            case "STUBSERVER_MASTER_CATALOG_TEST":
                return resolveMasterCatalogTable(appEnv);

            case "DailyMetrics":
            case "READYAPI_DAILY_METRICS":
                return "READYAPI_DAILY_METRICS";

            case "VsExecutionMode":
            case "VS_EXECUTIONMODE":
                return "VS_EXECUTIONMODE";

            case "VsLiveUrl":
            case "VS_LIVEURLS":
                return "VS_LIVEURLS";

            case "VsCatalog":
            case "READYAPI_VS_CATALOG":
                return "READYAPI_VS_CATALOG";

            // ===================== Java-Backend entities =====================

            // ---- Deployment-specific (QA / UAT only, no local) ----
            case "AuditLog":
            case "STUBSERVERAUDITLOGS_QA":
            case "STUBSERVERAUDITLOGS_UAT":
                return resolveAuditLogTable(appEnv);

            case "AssignedService":
            case "STUBASSIGNEDSERVICES_QA":
            case "STUBASSIGNEDSERVICES_UAT":
                return resolveAssignedServiceTable(appEnv);

            // ---- Static (same in every environment) ----
            case "User":
            case "STUBSERVERUSERS":
                return "STUBSERVERUSERS";

            case "RefreshToken":
            case "AUTH_REFRESH_TOKENS":
                return "AUTH_REFRESH_TOKENS";

            case "PortRange":
            case "READYAPI_PORT_RANGE":
                return "READYAPI_PORT_RANGE";

            case "MonthlyMetrics":
            case "READYAPI_MONTHLY_METRICS":
                return "READYAPI_MONTHLY_METRICS";

            case "ResponseTime":
            case "READYAPI_RESPONSE_TIME":
                return "READYAPI_RESPONSE_TIME";

            default:
                return logicalName;
        }
    }

    private String resolveVsDetailsTable(String appEnv) {

        switch (appEnv) {
            case "qa":
                return "STUBSERVERQA_VSDETAILS";

            case "uat":
                return "STUBSERVERUAT_VSDETAILS";

            case "local":
                return "STUBSERVER_VSDETAILS_TEST";

            default:
                throw new IllegalArgumentException(
                        "Unsupported app.env for VSDetails table: " + appEnv
                );
        }
    }

    private String resolveMasterCatalogTable(String appEnv) {

        switch (appEnv) {
            case "qa":
                return "STUBSERVER_MASTER_CATALOG_QA";

            case "uat":
                return "STUBSERVER_MASTER_CATALOG_UAT";

            case "local":
                return "STUBSERVER_MASTER_CATALOG_TEST";

            default:
                throw new IllegalArgumentException(
                        "Unsupported app.env for MasterCatalog table: " + appEnv
                );
        }
    }

    private String resolveAuditLogTable(String appEnv) {

        switch (appEnv) {
            case "qa":
                return "STUBSERVERAUDITLOGS_QA";

            case "uat":
                return "STUBSERVERAUDITLOGS_UAT";

            default:
                throw new IllegalArgumentException(
                        "Unsupported app.env for AuditLog table: " + appEnv
                );
        }
    }

    private String resolveAssignedServiceTable(String appEnv) {

        switch (appEnv) {
            case "qa":
                return "STUBASSIGNEDSERVICES_QA";

            case "uat":
                return "STUBASSIGNEDSERVICES_UAT";

            default:
                throw new IllegalArgumentException(
                        "Unsupported app.env for AssignedService table: " + appEnv
                );
        }
    }

    @Override
    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment env) {
        return name;
    }

    @Override
    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment env) {
        if (name == null) {
            return null;
        }

        return Identifier.toIdentifier(name.getText(), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment env) {
        if (name == null) {
            return null;
        }

        /*
         * Important:
         * Preserve quotes for reserved columns like:
         * @Column(name = "\"GROUP\"")
         * @Column(name = "\"COUNT\"")
         */
        return Identifier.toIdentifier(name.getText(), name.isQuoted());
    }

    @Override
    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment env) {
        if (name == null) {
            return null;
        }

        return Identifier.toIdentifier(name.getText(), name.isQuoted());
    }
}
