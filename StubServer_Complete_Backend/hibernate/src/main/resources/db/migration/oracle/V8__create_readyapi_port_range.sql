-- READYAPI_PORT_RANGE (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.READYAPI_PORT_RANGE (
            PORTID     NUMBER(15,0)  NOT NULL,
            APPNAME    VARCHAR2(100) NOT NULL,
            PORTS      VARCHAR2(150),
            UPDATETIME DATE,
            UPDATEDBY  VARCHAR2(50)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN   -- ORA-00955: name already used by an existing object
            RAISE;
        END IF;
END;
/
