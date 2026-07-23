-- READYAPI_VS_CATALOG (SQL Server) — created only if missing.
IF OBJECT_ID('${schema}.READYAPI_VS_CATALOG', 'U') IS NULL
BEGIN
    CREATE TABLE ${schema}.READYAPI_VS_CATALOG (
        MASTERID            BIGINT NOT NULL,
        VSNAME              VARCHAR(150) NOT NULL,
        TRANSPORTTYPE       VARCHAR(25) NOT NULL,
        VIRTSERVER          VARCHAR(50) NOT NULL,
        PORT                BIGINT NULL,
        UPDATETIME          DATETIME NULL,
        UPDATEDBY           VARCHAR(50) NULL,
        STATUS              VARCHAR(50) NULL,
        PARTNER             VARCHAR(300) NULL,
        TABLENAME           VARCHAR(100) NULL,
        INTERNAL_EXTERNAL   VARCHAR(10) NULL,
        ENV_TYPE            VARCHAR(20) NULL,
        HEALTHCHECK         VARCHAR(10) NULL,
        [GROUP]             VARCHAR(100) NULL,
        TAGS                VARCHAR(100) NULL,
        CONSTRAINT PK_READYAPI_VS_CATALOG PRIMARY KEY (MASTERID)
    );
END;
