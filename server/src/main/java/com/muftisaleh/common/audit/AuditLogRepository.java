package com.muftisaleh.common.audit;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    @Query("""
            select a from AuditLog a
            where (:entity is null or a.entity = :entity)
              and (:entityId is null or a.entityId = :entityId)
              and (:actorId is null or a.actorId = :actorId)
              and (cast(:since as Instant) is null or a.at >= :since)
              and (cast(:until as Instant) is null or a.at <= :until)
              and (cast(:cursorAt as Instant) is null
                   or a.at < :cursorAt
                   or (a.at = :cursorAt and a.id < :cursorId))
            order by a.at desc, a.id desc
            """)
    List<AuditLog> findPage(@Param("entity") String entity,
                            @Param("entityId") String entityId,
                            @Param("actorId") UUID actorId,
                            @Param("since") Instant since,
                            @Param("until") Instant until,
                            @Param("cursorAt") Instant cursorAt,
                            @Param("cursorId") Long cursorId,
                            Limit limit);
}
