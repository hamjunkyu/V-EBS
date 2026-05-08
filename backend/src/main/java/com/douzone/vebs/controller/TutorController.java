package com.douzone.vebs.controller;

import com.douzone.vebs.service.TutorService;
import com.douzone.vebs.entity.TutorTimeslot;
import com.douzone.vebs.dto.AvailableTutorDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/tutors")
@RequiredArgsConstructor
@Tag(name = "강사 (Tutor)", description = "강사 관련 API")
public class TutorController {

    private final TutorService tutorService;

    @Operation(summary = "특정 강사의 가능시간 조회")
    @GetMapping("/{tutorId}/timeslots")
    public List<TutorTimeslot> getTimeslots(@PathVariable String tutorId) {
        return tutorService.getTimeslotsByTutorId(tutorId);   // Service 경유
    }

    @Operation(summary = "수강 가능 강사 검색")
    @GetMapping("/available")
    public List<AvailableTutorDto> getAvailableTutors(
            @RequestParam List<Integer> dayOfWeeks,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
            @RequestParam Integer durationMinutes
    ) {
        return tutorService.findAvailableTutors(dayOfWeeks, startTime, durationMinutes);
    }
}
