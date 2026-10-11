package com.muftisaleh.common.audit;

public record AuditChange(String from, String to) {

    public static AuditChange set(String to) {
        return new AuditChange(null, to);
    }

    public static AuditChange cleared(String from) {
        return new AuditChange(from, null);
    }
}
