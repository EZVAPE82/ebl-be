package com.elfbarlounge.domain.content.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        SELECT e FROM Event e
         WHERE e.visible = true
           AND (e.startsAt IS NULL OR e.startsAt <= :now)
           AND (e.endsAt IS NULL OR e.endsAt >= :now)
         ORDER BY COALESCE(e.startsAt, e.createdAt) DESC
        """)
    Page<Event> findOngoing(@Param("now") LocalDateTime now, Pageable pageable);
}
