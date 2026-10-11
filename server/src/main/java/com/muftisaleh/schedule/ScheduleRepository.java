package com.muftisaleh.schedule;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<ScheduleEvent, UUID> {

    @Query("""
            select s from ScheduleEvent s
            where s.eventAt >= :from
            order by s.eventAt asc
            """)
    List<ScheduleEvent> findUpcoming(@Param("from") Instant from);

    @Query("select s from ScheduleEvent s order by s.eventAt desc")
    List<ScheduleEvent> findAllDesc();
}
