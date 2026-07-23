-- READYAPI_MONTHLY_METRICS (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.READYAPI_MONTHLY_METRICS (
            METRICSID  NUMBER(15,0) NOT NULL,
            VSNAME     VARCHAR2(150),
            "COUNT"    NUMBER(30,0),
            "MONTH"    VARCHAR2(30),
            "YEAR"     VARCHAR2(10),
            QACOUNT    NUMBER(30,0),
            PERFCOUNT  NUMBER(30,0),
            VIRTSERVER VARCHAR2(30)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN   -- ORA-00955: name already used by an existing object
            RAISE;
        END IF;
END;
/
