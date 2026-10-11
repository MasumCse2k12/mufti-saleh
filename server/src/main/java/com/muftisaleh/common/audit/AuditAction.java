package com.muftisaleh.common.audit;

public enum AuditAction {
    INSERT,
    UPDATE,
    DELETE,

    LOGIN,
    LOGIN_FAILED,
    LOCKOUT,
    PASSWORD_SET,
    ACCESS_REVOKED,
    ACCESS_RESTORED
}
