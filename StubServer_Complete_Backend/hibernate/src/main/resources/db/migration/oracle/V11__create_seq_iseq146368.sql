-- Shared ID-generator sequence ISEQ$$_146368 (used by VSDETAILS.VSID and, via
-- Hibernate's @SequenceGenerator, by DailyMetrics.METRICSID) — created only if missing.
-- NOTE: START WITH is set to 1 for a fresh DB. If you are seeding into an existing
-- dataset, adjust START WITH above the current max id. Verify against the real
-- sequence DDL: SELECT DBMS_METADATA.GET_DDL('SEQUENCE','ISEQ$$_146368') FROM dual;
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE SEQUENCE ${schema}.ISEQ$$_146368
            START WITH 1 INCREMENT BY 1 CACHE 20 NOCYCLE
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN   -- ORA-00955: name already used by an existing object
            RAISE;
        END IF;
END;
/
