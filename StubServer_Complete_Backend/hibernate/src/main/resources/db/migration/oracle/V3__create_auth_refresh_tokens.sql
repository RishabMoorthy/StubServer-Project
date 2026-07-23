-- AUTH_REFRESH_TOKENS (Oracle) — created only if missing (ignores ORA-00955).
BEGIN
    EXECUTE IMMEDIATE q'[
        CREATE TABLE ${schema}.AUTH_REFRESH_TOKENS (
            JTI         VARCHAR2(64)  NOT NULL,
            USERNAME    VARCHAR2(128) NOT NULL,
            EXPIRES_AT  TIMESTAMP(6)  NOT NULL,
            REVOKED     CHAR(1) DEFAULT 'N',
            ISSUED_AT   TIMESTAMP(6) DEFAULT SYSTIMESTAMP,
            USER_AGENT  VARCHAR2(512),
            IP          VARCHAR2(64),
            CONSTRAINT PK_AUTH_REFRESH_TOKENS PRIMARY KEY (JTI),
            CONSTRAINT CK_AUTH_REFRESH_TOKENS_REVOKED CHECK (REVOKED IN ('Y','N'))
        )
    ]';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -955 THEN   -- ORA-00955: name already used by an existing object
            RAISE;
        END IF;
END;
/
