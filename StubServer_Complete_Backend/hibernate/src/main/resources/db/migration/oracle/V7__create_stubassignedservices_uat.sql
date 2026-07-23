-- STUBASSIGNEDSERVICES_UAT (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.STUBASSIGNEDSERVICES_UAT (
            USERNAME    VARCHAR2(255),
            SERVICENAME VARCHAR2(255)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN   -- ORA-00955: name already used by an existing object
            RAISE;
        END IF;
END;
/
