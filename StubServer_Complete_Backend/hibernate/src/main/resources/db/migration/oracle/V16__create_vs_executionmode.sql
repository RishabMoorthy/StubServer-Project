-- VS_EXECUTIONMODE (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.VS_EXECUTIONMODE (
            VSID          NUMBER(15,0) NOT NULL,
            MASTERID      VARCHAR2(100) NOT NULL,
            VIRTSERVER    VARCHAR2(50),
            EXECUTIONMODE VARCHAR2(50),
            UPDATETIME    DATE,
            UPDATEDBY     VARCHAR2(50)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN
            RAISE;
        END IF;
END;
/
