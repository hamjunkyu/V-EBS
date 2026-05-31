package com.douzone.vebs.controller;

import com.douzone.vebs.service.EnrollmentService;
import com.douzone.vebs.dto.AttendanceRequestDto;
import com.douzone.vebs.dto.AttendanceResponseDto;
import com.douzone.vebs.dto.TimeSlotDto;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "수강신청 (Attendance)", description = "수강신청 관련 API")
public class AttendanceController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "수강 가능 시간 조회")
    @GetMapping("/available-times")
    public List<TimeSlotDto> getAvailableTimeSlots(
            @RequestParam Integer dayPattern,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate
    ) {
        return enrollmentService.findAvailableTimeSlots(dayPattern, startDate);
    }

    @Operation(summary = "수강신청 등록")
    @PostMapping
    public AttendanceResponseDto registerEnrollment(@RequestBody @Valid AttendanceRequestDto request) {
        return enrollmentService.registerEnrollment(request);
    }
}
