package com.muftisaleh.common.audit;

import org.hibernate.event.spi.AutoFlushEvent;
import org.hibernate.event.spi.AutoFlushEventListener;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditFlushListener implements FlushEventListener, AutoFlushEventListener {

    private final AuditTrail trail;

    @Override
    public void onFlush(FlushEvent event) {
        trail.drain();
    }

    @Override
    public void onAutoFlush(AutoFlushEvent event) {
        trail.drain();
    }
}
