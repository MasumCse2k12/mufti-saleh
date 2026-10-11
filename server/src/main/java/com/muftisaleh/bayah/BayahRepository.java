package com.muftisaleh.bayah;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BayahRepository extends JpaRepository<BayahRequest, UUID> {

    @Query("""
            select b from BayahRequest b
            where (:status is null or b.status = :status)
            order by b.createdAt desc
            """)
    List<BayahRequest> findByStatus(@Param("status") BayahStatus status, Limit limit);
}
