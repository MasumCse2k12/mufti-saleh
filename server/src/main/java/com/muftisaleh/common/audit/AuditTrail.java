package com.muftisaleh.common.audit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.json.JsonMapper;

/**
 * Writes audit rows on the same JDBC connection as the change they describe,
 * inside the same transaction. A row written on a separate connection can
 * commit while the change it records rolls back — that is a false record, which
 * is worse than no record at all.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditTrail {

    private static final String PENDING_KEY = AuditTrail.class.getName() + ".pending";

    private static final String INSERT_SQL = """
            insert into audit_log (at, action, entity, entity_id,
                                   actor_id, actor_kind, actor_label, note, changes,
                                   request_id, ip, method, path)
            values (?, ?, ?, ?, ?, ?, ?, ?, cast(? as jsonb), ?, ?, ?, ?)
            """;

    private static final JsonMapper MAPPER = JsonMapper.builder().build();

    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;

    public void record(AuditAction action, String entity, String entityId,
                       Map<String, AuditChange> changes) {
        Pending pending = pending();
        pending.rows.add(new Row(Instant.now(), action, entity, entityId,
                AuditContext.current(), changes == null ? Map.of() : changes));
        if (pending.standalone) {
            writeStandalone(pending.take());
        }
    }

    public void record(AuditAction action, String entity, String entityId) {
        record(action, entity, entityId, Map.of());
    }

    public void note(String note) {
        if (note != null && !note.isBlank()) {
            pending().note = note.strip();
        }
    }

    private Pending pending() {
        Object existing = TransactionSynchronizationManager.getResource(PENDING_KEY);
        if (existing instanceof Pending pending) {
            return pending;
        }

        Pending pending = new Pending();
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.bindResource(PENDING_KEY, pending);
            TransactionSynchronizationManager.registerSynchronization(new Flusher());
        } else {
            pending.standalone = true;
            log.warn("Audit row recorded outside a transaction");
        }
        return pending;
    }

    void drain() {
        Object existing = TransactionSynchronizationManager.getResource(PENDING_KEY);
        if (!(existing instanceof Pending pending) || pending.rows.isEmpty()) {
            return;
        }

        List<Row> rows = pending.take();
        EntityManager em = EntityManagerFactoryUtils.getTransactionalEntityManager(entityManagerFactory);
        if (em == null) {
            writeStandalone(rows);
            return;
        }
        em.unwrap(Session.class).doWork(connection -> insert(connection, rows, pending.note));
    }

    private final class Flusher implements TransactionSynchronization {

        @Override
        public void beforeCommit(boolean readOnly) {
            drain();
        }

        @Override
        public void afterCompletion(int status) {
            TransactionSynchronizationManager.unbindResourceIfPossible(PENDING_KEY);
        }
    }

    private void writeStandalone(List<Row> rows) {
        if (rows.isEmpty()) {
            return;
        }
        Connection connection = DataSourceUtils.getConnection(dataSource);
        try {
            insert(connection, rows, null);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not write the audit trail", e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

    private void insert(Connection connection, List<Row> rows, String transactionNote) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            for (Row row : rows) {
                AuditActor actor = row.actor();
                statement.setObject(1, OffsetDateTime.ofInstant(row.at(), ZoneOffset.UTC));
                statement.setString(2, row.action().name());
                statement.setString(3, row.entity());
                statement.setString(4, row.entityId());
                statement.setObject(5, actor.adminId(), Types.OTHER);
                statement.setString(6, actor.kind().name());
                statement.setString(7, actor.label());
                statement.setString(8, transactionNote);
                statement.setString(9, json(row.changes()));
                statement.setString(10, actor.requestId());
                statement.setString(11, actor.ip());
                statement.setString(12, actor.method());
                statement.setString(13, actor.path());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private static String json(Map<String, AuditChange> changes) {
        Map<String, Map<String, String>> payload = new LinkedHashMap<>();
        changes.forEach((field, change) -> {
            Map<String, String> sides = new LinkedHashMap<>();
            sides.put("from", change.from());
            sides.put("to", change.to());
            payload.put(field, sides);
        });
        return MAPPER.writeValueAsString(payload);
    }

    private static final class Pending {
        private final List<Row> rows = new ArrayList<>();
        private String note;
        private boolean standalone;

        private List<Row> take() {
            List<Row> taken = List.copyOf(rows);
            rows.clear();
            return taken;
        }
    }

    private record Row(Instant at, AuditAction action, String entity, String entityId,
                       AuditActor actor, Map<String, AuditChange> changes) {
    }
}
