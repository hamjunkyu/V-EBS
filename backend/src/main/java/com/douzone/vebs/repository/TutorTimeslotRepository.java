package com.douzone.vebs.repository;

import com.douzone.vebs.entity.TutorTimeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TutorTimeslotRepository extends JpaRepository<TutorTimeslot, Long> {
    List<TutorTimeslot> findByTutorIdOrderByStartTime(String tutorId);
}
