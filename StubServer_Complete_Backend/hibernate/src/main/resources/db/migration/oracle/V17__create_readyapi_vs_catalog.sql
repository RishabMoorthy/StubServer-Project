-- READYAPI_VS_CATALOG (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.READYAPI_VS_CATALOG (
            MASTERID          NUMBER(15,0) NOT NULL,
            VSNAME            VARCHAR2(150) NOT NULL,
            TRANSPORTTYPE     VARCHAR2(25) NOT NULL,
            VIRTSERVER        VARCHAR2(50) NOT NULL,
            PORT              NUMBER(15,0),
            UPDATETIME        DATE,
            UPDATEDBY         VARCHAR2(50),
            STATUS            VARCHAR2(50),
            PARTNER           VARCHAR2(300),
            TABLENAME         VARCHAR2(100),
            INTERNAL_EXTERNAL VARCHAR2(10),
            ENV_TYPE          VARCHAR2(20),
            HEALTHCHECK       VARCHAR2(10),
            "GROUP"           VARCHAR2(100),
            TAGS              VARCHAR2(100)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN
            RAISE;
        END IF;
END;
/
