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
            "AND t.startTime <= :requestStart " +
            "AND t.endTime >= :requestEnd " +
            "ORDER BY t.tutorId, t.dayOfWeek")
    List<TutorTimeslot> findAvailableSlots(
            @Param("dayOfWeeks") List<Integer> dayOfWeeks,
            @Param("requestStart") LocalTime requestStart,
            @Param("requestEnd") LocalTime requestEnd
    );

    /**
     * 해당 요일들의 가용시간 전체 조회 (가용시간 조회 시 한 번만 조회해 메모리에서 슬롯별 필터링).
     */
    @Query("SELECT t FROM TutorTimeslot t " +
            "WHERE t.dayOfWeek IN :dayOfWeeks " +
            "ORDER BY t.tutorId, t.dayOfWeek")
    List<TutorTimeslot> findByDayOfWeeks(@Param("dayOfWeeks") List<Integer> dayOfWeeks);

}
