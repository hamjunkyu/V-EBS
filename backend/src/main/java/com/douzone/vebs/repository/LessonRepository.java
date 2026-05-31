package com.douzone.vebs.repository;

import com.douzone.vebs.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

    /**
     * 특정 강사들이 기간 내 가진 활성 수업 조회 (충돌 검사 후보).
     * clsStat: 1=예정, 2=완료, 5=보강 (3=취소, 4=결석은 시간 해방으로 간주)
     */
    @Query("SELECT l FROM Lesson l " +
            "WHERE l.tutorId IN :tutorIds " +
            "AND l.clsDate BETWEEN :startDate AND :endDate " +
            "AND l.clsStat IN (1, 2, 5)")
    List<Lesson> findConflictCandidates(
            @Param("tutorIds") List<String> tutorIds,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
