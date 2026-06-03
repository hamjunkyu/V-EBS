package com.douzone.vebs.service;

import com.douzone.vebs.dto.AttendanceRequestDto;
import com.douzone.vebs.dto.AttendanceResponseDto;
import com.douzone.vebs.dto.TimeSlotDto;
import com.douzone.vebs.entity.Attendance;
import com.douzone.vebs.entity.Lesson;
import com.douzone.vebs.entity.TutorTimeslot;
import com.douzone.vebs.exception.InvalidStartDateException;
import com.douzone.vebs.exception.NoAvailableTutorException;
import com.douzone.vebs.exception.TutorConflictException;
import com.douzone.vebs.repository.AttendanceRepository;
import com.douzone.vebs.repository.LessonRepository;
import com.douzone.vebs.repository.TutorTimeslotRepository;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final TutorTimeslotRepository tutorRepo;
    private final AttendanceRepository attendanceRepo;
    private final LessonRepository lessonRepo;

    private static final int LESSON_COUNT = 32;
    private static final int FIXED_DURATION_MIN = 20;

    private static final List<LocalTime> TIME_SLOTS = List.of(
            LocalTime.of(12, 0),  LocalTime.of(12, 25), LocalTime.of(12, 50),
            LocalTime.of(13, 15), LocalTime.of(13, 40), LocalTime.of(14, 5),
            LocalTime.of(14, 30), LocalTime.of(15, 0),  LocalTime.of(15, 25),
            LocalTime.of(15, 50), LocalTime.of(16, 15), LocalTime.of(16, 40),
            LocalTime.of(17, 5),  LocalTime.of(17, 30), LocalTime.of(18, 0),
            LocalTime.of(18, 25), LocalTime.of(18, 50), LocalTime.of(19, 20),
            LocalTime.of(19, 45), LocalTime.of(20, 10), LocalTime.of(20, 35),
            LocalTime.of(21, 0),  LocalTime.of(21, 25), LocalTime.of(21, 50),
            LocalTime.of(22, 15), LocalTime.of(22, 40)
    );

    /**
     * 가능 시간 조회
     * 각 시간 슬롯별로 가능 강사, 충돌 검사 후 자동 배정 결과 반환
     */
    public List<TimeSlotDto> findAvailableTimeSlots(int dayPattern, LocalDate startDate) {
        validateStartDate(startDate, dayPattern);

        List<Integer> lessonDays = getLessonDays(dayPattern);
        LocalDate endDate = calculateEndDate(startDate, dayPattern);

        List<TutorTimeslot> allTutorSlots = tutorRepo.findByDayOfWeeks(lessonDays);
        List<Lesson> existingLessons = lessonRepo.findActiveInPeriod(startDate, endDate);

        List<TimeSlotDto> timeSlots = new ArrayList<>();
        for (LocalTime slotStart : TIME_SLOTS) {
            LocalTime slotEnd = slotStart.plusMinutes(FIXED_DURATION_MIN);

            List<String> candidateTutors = filterCandidateTutors(allTutorSlots, lessonDays, slotStart, slotEnd);
            if (candidateTutors.isEmpty()) {
                timeSlots.add(new TimeSlotDto(slotStart, false, null));
                continue;
            }

            List<String> availableTutors = new ArrayList<>();
            for (String tutorId : candidateTutors) {
                if (!hasConflict(existingLessons, tutorId, slotStart, slotEnd, lessonDays)) {
                    availableTutors.add(tutorId);
                }
            }

            if (availableTutors.isEmpty()) {
                timeSlots.add(new TimeSlotDto(slotStart, false, null));
            } else {
                Collections.sort(availableTutors);
                timeSlots.add(new TimeSlotDto(slotStart, true, availableTutors.getFirst()));
            }
        }

        return timeSlots;
    }

    /**
     * 수강신청 등록
     * 트랜잭션 안에서 충돌 재검사, 자동 배정, TBL_ATTENDANCE/TBL_CLASS 일괄 INSERT
     */
    @Transactional
    public AttendanceResponseDto registerEnrollment(AttendanceRequestDto request) {
        validateStartDate(request.getStartDate(), request.getDayPattern());

        List<Integer> lessonDays = getLessonDays(request.getDayPattern());
        List<LocalDate> lessonDates = generateLessonDates(request.getStartDate(), lessonDays, LESSON_COUNT);
        LocalDate endDate = lessonDates.getLast();
        LocalTime slotEnd = request.getStartTime().plusMinutes(FIXED_DURATION_MIN);

        List<String> candidateTutors = findCandidateTutors(lessonDays, request.getStartTime(), slotEnd);
        if (candidateTutors.isEmpty()) {
            throw new NoAvailableTutorException("해당 시간에 가능한 강사가 없습니다");
        }

        List<Lesson> existingLessons = lessonRepo.findConflictCandidates(
                candidateTutors, request.getStartDate(), endDate);

        List<String> availableTutors = new ArrayList<>();
        for (String tutorId : candidateTutors) {
            if (!hasConflict(existingLessons, tutorId, request.getStartTime(), slotEnd, lessonDays)) {
                availableTutors.add(tutorId);
            }
        }

        if (availableTutors.isEmpty()) {
            throw new TutorConflictException("선택한 시간에 모든 강사가 다른 수업과 겹칩니다");
        }

        Collections.sort(availableTutors);
        String assignedTutorId = availableTutors.getFirst();

        Attendance attendance = new Attendance();
        attendance.setStudentId(request.getStudentId());
        attendance.setStudentSeq(request.getStudentSeq());
        attendance.setStartDate(request.getStartDate());
        attendance.setEndDate(endDate);
        attendance.setLessonCount(LESSON_COUNT);
        attendance.setDayPattern(request.getDayPattern());
        attendance.setCourseType(request.getCourseType());
        attendance.setStartTime(request.getStartTime());
        attendance.setDurationMin(FIXED_DURATION_MIN);
        attendance.setTutorId(assignedTutorId);
        attendance.setAttStat(2);
        attendanceRepo.save(attendance);

        List<Lesson> lessons = new ArrayList<>();
        for (int i = 0; i < lessonDates.size(); i++) {
            Lesson lesson = new Lesson();
            lesson.setAttSeq(attendance.getAttSeq());
            lesson.setClsNo(i + 1);
            lesson.setClsDate(lessonDates.get(i));
            lesson.setClsTime(request.getStartTime());
            lesson.setDurationMin(FIXED_DURATION_MIN);
            lesson.setTutorId(assignedTutorId);
            lesson.setClsStat(1);
            lessons.add(lesson);
        }
        lessonRepo.saveAll(lessons);

        return new AttendanceResponseDto(attendance.getAttSeq(), endDate, LESSON_COUNT, assignedTutorId);
    }

    private void validateStartDate(LocalDate startDate, int dayPattern) {
        if (startDate.isAfter(LocalDate.now().plusYears(1))) {
            throw new InvalidStartDateException("시작일은 신청일로부터 1년 이내여야 합니다");
        }

        List<Integer> lessonDays = getLessonDays(dayPattern);
        int dayOfWeek = startDate.getDayOfWeek().getValue();
        if (!lessonDays.contains(dayOfWeek)) {
            throw new InvalidStartDateException("시작일은 선택한 수업요일과 일치해야 합니다");
        }
    }

    private List<Integer> getLessonDays(int dayPattern) {
        if (dayPattern == 1) {
            return List.of(1, 3, 5);
        } else if (dayPattern == 2) {
            return List.of(2, 4);
        } else {
            throw new IllegalArgumentException("Invalid dayPattern: " + dayPattern);
        }
    }

    private LocalDate calculateEndDate(LocalDate startDate, int dayPattern) {
        List<Integer> lessonDays = getLessonDays(dayPattern);
        List<LocalDate> lessonDates = generateLessonDates(startDate, lessonDays, LESSON_COUNT);
        return lessonDates.getLast();
    }

    /**
     * 미리 조회한 가용시간 목록에서 해당 슬롯에 가능한 강사를 추린다 (DB 조회 없이 메모리에서 처리).
     */
    private List<String> filterCandidateTutors(List<TutorTimeslot> allTutorSlots, List<Integer> lessonDays,
                                               LocalTime slotStart, LocalTime slotEnd) {
        Map<String, List<TutorTimeslot>> slotsByTutorId = new HashMap<>();
        for (TutorTimeslot slot : allTutorSlots) {
            if (slot.getStartTime().isAfter(slotStart)) continue;
            if (slot.getEndTime().isBefore(slotEnd)) continue;

            String tutorId = slot.getTutorId();
            if (!slotsByTutorId.containsKey(tutorId)) {
                slotsByTutorId.put(tutorId, new ArrayList<>());
            }
            slotsByTutorId.get(tutorId).add(slot);
        }

        List<String> candidates = new ArrayList<>();
        for (Map.Entry<String, List<TutorTimeslot>> entry : slotsByTutorId.entrySet()) {
            if (entry.getValue().size() == lessonDays.size()) {
                candidates.add(entry.getKey());
            }
        }
        return candidates;
    }

    /**
     * 특정 강사의 시간 충돌 여부 검사
     */
    private boolean hasConflict(List<Lesson> existingLessons, String tutorId,
                                LocalTime slotStart, LocalTime slotEnd,
                                List<Integer> lessonDays) {
        for (Lesson lesson : existingLessons) {
            if (!lesson.getTutorId().equals(tutorId)) continue;

            int lessonDayOfWeek = lesson.getClsDate().getDayOfWeek().getValue();
            if (!lessonDays.contains(lessonDayOfWeek)) continue;

            LocalTime lessonEnd = lesson.getClsTime().plusMinutes(lesson.getDurationMin());
            if (lesson.getClsTime().isBefore(slotEnd) && lessonEnd.isAfter(slotStart)) {
                return true;
            }
        }
        return false;
    }

    private List<LocalDate> generateLessonDates(LocalDate startDate, List<Integer> lessonDays, int lessonCount) {
        List<LocalDate> lessonDates = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (lessonDates.size() < lessonCount) {
            if (lessonDays.contains(currentDate.getDayOfWeek().getValue())) {
                lessonDates.add(currentDate);
            }
            currentDate = currentDate.plusDays(1);
        }
        return lessonDates;
    }

    /**
     * 시간 슬롯에 가능한 강사 조회 (모든 lessonDays에 가능해야)
     */
    private List<String> findCandidateTutors(List<Integer> lessonDays, LocalTime slotStart, LocalTime slotEnd) {
        List<TutorTimeslot> slots = tutorRepo.findAvailableSlots(lessonDays, slotStart, slotEnd);

        Map<String, List<TutorTimeslot>> slotsByTutorId = new HashMap<>();
        for (TutorTimeslot slot : slots) {
            String tutorId = slot.getTutorId();
            if (!slotsByTutorId.containsKey(tutorId)) {
                slotsByTutorId.put(tutorId, new ArrayList<>());
            }
            slotsByTutorId.get(tutorId).add(slot);
        }

        List<String> candidates = new ArrayList<>();
        for (Map.Entry<String, List<TutorTimeslot>> entry : slotsByTutorId.entrySet()) {
            if (entry.getValue().size() == lessonDays.size()) {
                candidates.add(entry.getKey());
            }
        }
        return candidates;
    }
}
