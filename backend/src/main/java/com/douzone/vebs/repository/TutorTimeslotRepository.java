package com.douzone.vebs.repository;

import com.douzone.vebs.entity.TutorTimeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalTime;

public interface TutorTimeslotRepository extends JpaRepository<TutorTimeslot, Long> {
    List<TutorTimeslot> findByTutorIdOrderByStartTime(String tutorId);

    @Query("SELECT t FROM TutorTimeslot t " +
            "WHERE t.dayOfWeek IN :dayOfWeeks " +
            "AND t.startTime <= :reqStart " +
            "AND t.endTime >= :reqEnd " +
            "ORDER BY t.tutorId, t.dayOfWeek")
    List<TutorTimeslot> findAvailableSlots(
            @Param("dayOfWeeks") List<Integer> dayOfWeeks,
            @Param("reqStart") LocalTime reqStart,
            @Param("reqEnd") LocalTime reqEnd
    );
}
