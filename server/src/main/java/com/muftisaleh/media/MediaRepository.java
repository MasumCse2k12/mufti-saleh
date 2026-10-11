package com.muftisaleh.media;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MediaRepository extends JpaRepository<MediaItem, UUID> {

    @Query("""
            select m from MediaItem m
            where (:type is null or m.type = :type)
              and (cast(:cursor as Instant) is null or m.publishedAt < :cursor)
            order by m.publishedAt desc
            """)
    List<MediaItem> findPublished(@Param("type") MediaType type,
                                  @Param("cursor") Instant cursor,
                                  Limit limit);
}
