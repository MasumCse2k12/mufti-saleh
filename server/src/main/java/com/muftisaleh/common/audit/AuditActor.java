package com.muftisaleh.common.audit;

import java.util.UUID;

public record AuditActor(
        UUID adminId,
        ActorKind kind,
        String label,
        String requestId,
        String ip,
        String method,
        String path) {

    public static final AuditActor UNATTRIBUTED = system("unattributed");

    public static AuditActor system(String what) {
        return new AuditActor(null, ActorKind.SYSTEM, "system:" + what, null, null, null, null);
    }

    public AuditActor identified(UUID adminId, ActorKind kind, String label) {
        return new AuditActor(adminId, kind, label, requestId, ip, method, path);
    }
}
