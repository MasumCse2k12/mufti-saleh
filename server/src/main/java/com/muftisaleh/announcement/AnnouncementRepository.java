package com.muftisaleh.announcement;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {

    @Query("""
            select a from Announcement a
            where (a.expiresAt is null or a.expiresAt > :now)
              and (cast(:cursor as Instant) is null or a.publishedAt < :cursor)
            order by a.pinned desc, a.publishedAt desc
            """)
    List<Announcement> findActive(@Param("now") Instant now,
                                  @Param("cursor") Instant cursor,
                                  Limit limit);

    @Query("select a from Announcement a order by a.publishedAt desc")
    List<Announcement> findAllForAdmin(Limit limit);
}
