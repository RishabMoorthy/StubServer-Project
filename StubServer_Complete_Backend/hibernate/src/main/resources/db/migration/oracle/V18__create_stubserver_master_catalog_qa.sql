-- STUBSERVER_MASTER_CATALOG_QA (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.STUBSERVER_MASTER_CATALOG_QA (
            MASTERID           NUMBER(15,0) NOT NULL,
            VSNAME             VARCHAR2(150) NOT NULL,
            UPDATETIME         DATE,
            STATUS             VARCHAR2(50),
            BACKENDAPPLICATION VARCHAR2(300),
            BACKENDTYPE        VARCHAR2(10),
            ENV_TYPE           VARCHAR2(20),
            "GROUP"            VARCHAR2(100),
            PORT               NUMBER(10,0)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN
            RAISE;
        END IF;
END;
/
