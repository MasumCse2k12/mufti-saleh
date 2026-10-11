package com.muftisaleh.masail;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MasailRepository extends JpaRepository<Masail, UUID> {

    @Query("""
            select m from Masail m
            where m.published = true
              and (:category is null or m.category = :category)
            order by m.createdAt desc
            """)
    List<Masail> findPublished(@Param("category") String category, Limit limit);

    @Query("select m from Masail m order by m.createdAt desc")
    List<Masail> findAllDesc(Limit limit);
}
