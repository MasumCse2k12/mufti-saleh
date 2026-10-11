package com.muftisaleh.common.audit;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditListenerRegistrar {

    private final EntityManagerFactory entityManagerFactory;
    private final EntityAuditListener listener;
    private final AuditFlushListener flushListener;

    @PostConstruct
    void register() {
        SessionFactoryImplementor sessionFactory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        EventListenerRegistry registry = sessionFactory.getEventEngine().getListenerRegistry();

        registry.appendListeners(EventType.POST_INSERT, listener);
        registry.appendListeners(EventType.POST_UPDATE, listener);
        registry.appendListeners(EventType.POST_DELETE, listener);
        registry.appendListeners(EventType.FLUSH, flushListener);
        registry.appendListeners(EventType.AUTO_FLUSH, flushListener);

        log.info("Audit trail active");
    }
}
