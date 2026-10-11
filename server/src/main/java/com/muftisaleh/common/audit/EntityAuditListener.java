package com.muftisaleh.common.audit;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;

import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EntityAuditListener
        implements PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener {

    private final AuditTrail trail;
    private final Map<Class<?>, String> tableNames = new ConcurrentHashMap<>();

    @Override
    public void onPostInsert(PostInsertEvent event) {
        EntityPersister persister = event.getPersister();
        if (notAudited(persister)) {
            return;
        }
        trail.record(AuditAction.INSERT, table(persister), id(event.getId()),
                AuditChanges.ofInsert(persister.getPropertyNames(),
                        render(event.getState(), persister, event.getSession())));
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        EntityPersister persister = event.getPersister();
        if (notAudited(persister)) {
            return;
        }
        Map<String, AuditChange> changes = AuditChanges.ofUpdate(
                persister.getPropertyNames(),
                render(event.getOldState(), persister, event.getSession()),
                render(event.getState(), persister, event.getSession()),
                event.getDirtyProperties());
        if (changes.isEmpty()) {
            return;
        }
        trail.record(AuditAction.UPDATE, table(persister), id(event.getId()), changes);
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        EntityPersister persister = event.getPersister();
        if (notAudited(persister)) {
            return;
        }
        trail.record(AuditAction.DELETE, table(persister), id(event.getId()),
                AuditChanges.ofDelete(persister.getPropertyNames(),
                        render(event.getDeletedState(), persister, event.getSession())));
    }

    private String[] render(Object[] state, EntityPersister persister, SharedSessionContractImplementor session) {
        Type[] types = persister.getPropertyTypes();
        String[] rendered = new String[types.length];
        if (state == null) {
            return rendered;
        }
        for (int i = 0; i < types.length; i++) {
            Object value = state[i];
            if (value == null || types[i].isCollectionType()) {
                continue;
            }
            if (types[i].isEntityType()) {
                value = session.getContextEntityIdentifier(value);
            }
            rendered[i] = AuditChanges.render(value);
        }
        return rendered;
    }

    private boolean notAudited(EntityPersister persister) {
        return AuditLog.class.equals(persister.getMappedClass());
    }

    private String table(EntityPersister persister) {
        return tableNames.computeIfAbsent(persister.getMappedClass(), type -> {
            Table table = type.getAnnotation(Table.class);
            return table != null && !table.name().isBlank()
                    ? table.name()
                    : type.getSimpleName().toLowerCase(Locale.ROOT);
        });
    }

    private String id(Object id) {
        return String.valueOf(id);
    }
}
