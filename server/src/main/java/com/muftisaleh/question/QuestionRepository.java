package com.muftisaleh.question;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    /** Public: answered questions, optionally filtered by category, cursor-paged. */
    @Query("""
            select q from Question q
            where q.status = 'ANSWERED'
              and (:category is null or q.category = :category)
              and (cast(:cursor as Instant) is null or q.answeredAt < :cursor)
            order by q.answeredAt desc
            """)
    List<Question> findAnswered(@Param("category") QuestionCategory category,
                                @Param("cursor") Instant cursor,
                                Limit limit);

    /** Admin: pending questions, newest first. */
    @Query("select q from Question q where q.status = 'PENDING' order by q.createdAt asc")
    List<Question> findPending(Limit limit);

    /** Admin: all questions, newest first. */
    @Query("select q from Question q order by q.createdAt desc")
    List<Question> findAllDesc(Limit limit);
}
