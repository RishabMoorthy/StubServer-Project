-- READYAPI_RESPONSE_TIME (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.READYAPI_RESPONSE_TIME (
            RESPID      NUMBER(15,0) NOT NULL,
            METRICSID   NUMBER(15,0) NOT NULL,
            STARTTIME   DATE NOT NULL,
            ENDTIME     DATE NOT NULL,
            AVGRESPTIME NUMBER(30,0),
            MAXRESPTIME NUMBER(30,0),
            AVGTPS      NUMBER(30,0)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN   -- ORA-00955: name already used by an existing object
            RAISE;
        END IF;
END;
/
