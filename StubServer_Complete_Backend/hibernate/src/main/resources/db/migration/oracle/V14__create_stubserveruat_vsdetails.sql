-- STUBSERVERUAT_VSDETAILS (Oracle) — created only if missing (ignores ORA-00955).
-- NOTE: UAT has an extra XMLCONTENT (CLOB) column that QA does not have.
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.STUBSERVERUAT_VSDETAILS (
            VSID                NUMBER DEFAULT ${schema}.ISEQ$$_146368.NEXTVAL NOT NULL,
            VSNAME              VARCHAR2(255),
            PORT                NUMBER(10,0),
            LASTUPDATED         VARCHAR2(30),
            USERNAME            VARCHAR2(255),
            STATUS              VARCHAR2(10),
            XMLCONTENT          CLOB,
            KEEPREQRESLOGS      VARCHAR2(10) DEFAULT 'No',
            KEEPREQRESLOGSDAYS  VARCHAR2(20) DEFAULT '15',
            SAVERESPTIME        VARCHAR2(20) DEFAULT 'No',
            "GROUP"             VARCHAR2(100),
            TAGS                VARCHAR2(100),
            DELAYMODE           VARCHAR2(50) DEFAULT 'FIXED',
            DELAY               NUMBER DEFAULT 0,
            LOWERMS             NUMBER DEFAULT 0,
            UPPERMS             NUMBER DEFAULT 0,
            SIGMA               NUMBER(10,4) DEFAULT 0.0,
            MEDIANMS            NUMBER(10,4) DEFAULT 0.0,
            TOTALTXN            NUMBER(10,0) DEFAULT 0,
            DELAYPERCENT        NUMBER(10,0) DEFAULT 0,
            DATASOURCEENABLED   VARCHAR2(10),
            CONSTRAINT PK_STUBSERVERUAT_VSDETAILS PRIMARY KEY (VSID)
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN
            RAISE;
        END IF;
END;
/
