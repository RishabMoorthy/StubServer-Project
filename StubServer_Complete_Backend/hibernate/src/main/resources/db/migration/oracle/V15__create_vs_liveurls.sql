-- VS_LIVEURLS (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.VS_LIVEURLS (
            VSURLID    NUMBER(15,0) NOT NULL,
            VSID       NUMBER(15,0) NOT NULL,
            HOST       VARCHAR2(200),
            ISACTIVE   VARCHAR2(2),
            UPDATETIME DATE,
            UPDATEDBY  VARCHAR2(50)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN
            RAISE;
        END IF;
END;
/
