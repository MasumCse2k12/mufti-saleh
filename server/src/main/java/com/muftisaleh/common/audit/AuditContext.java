package com.muftisaleh.common.audit;

public final class AuditContext {

    static final ScopedValue<AuditActor> CURRENT = ScopedValue.newInstance();

    private AuditContext() {
    }

    public static AuditActor current() {
        return CURRENT.orElse(AuditActor.UNATTRIBUTED);
    }

    public static void runAs(AuditActor actor, Runnable body) {
        ScopedValue.where(CURRENT, actor).run(body);
    }
}
