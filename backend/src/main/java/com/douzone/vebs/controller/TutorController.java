package com.douzone.vebs.controller;

import com.douzone.vebs.entity.TutorTimeslot;
import com.douzone.vebs.repository.TutorTimeslotRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tutors")
public class TutorController {

    private final TutorTimeslotRepository repository;

    public TutorController(TutorTimeslotRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{tutorId}/timeslots")
    public List<TutorTimeslot> getTimeslots(@PathVariable String tutorId) {
        return repository.findByTutorIdOrderByStartTime(tutorId);
    }
}
