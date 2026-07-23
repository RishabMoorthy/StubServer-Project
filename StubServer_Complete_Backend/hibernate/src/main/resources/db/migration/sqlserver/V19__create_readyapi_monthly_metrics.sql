-- READYAPI_MONTHLY_METRICS (SQL Server) — created only if missing.
IF OBJECT_ID('${schema}.READYAPI_MONTHLY_METRICS', 'U') IS NULL
BEGIN
    CREATE TABLE ${schema}.READYAPI_MONTHLY_METRICS (
        METRICSID   BIGINT       NOT NULL,
        VSNAME      VARCHAR(150) NULL,
        [COUNT]     BIGINT       NULL,
        [MONTH]     VARCHAR(30)  NULL,
        [YEAR]      VARCHAR(10)  NULL,
        QACOUNT     BIGINT       NULL,
        PERFCOUNT   BIGINT       NULL,
        VIRTSERVER  VARCHAR(30)  NULL
    );
END;
