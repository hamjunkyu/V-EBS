package com.douzone.vebs.service;

import com.douzone.vebs.repository.TutorTimeslotRepository;
import com.douzone.vebs.dto.AvailableTutorDto;
import com.douzone.vebs.dto.MatchedSlotDto;
import com.douzone.vebs.entity.TutorTimeslot;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TutorService {

    private final TutorTimeslotRepository repository;

    public List<TutorTimeslot> getTimeslotsByTutorId(String tutorId) {
        return repository.findByTutorIdOrderByStartTime(tutorId);
    }

    public List<AvailableTutorDto> findAvailableTutors(
            List<Integer> dayOfWeeks,
            LocalTime startTime,
            Integer durationMinutes
    ) {
        LocalTime endTime = startTime.plusMinutes(durationMinutes);
        List<TutorTimeslot> slots = repository.findAvailableSlots(dayOfWeeks, startTime, endTime);

        Map<String, List<TutorTimeslot>> slotsByTutorId = new HashMap<>();
        for (TutorTimeslot slot : slots) {
            String tutorId = slot.getTutorId();
            if (!slotsByTutorId.containsKey(tutorId)) {
                slotsByTutorId.put(tutorId, new ArrayList<>());
            }
            slotsByTutorId.get(tutorId).add(slot);
        }

        List<AvailableTutorDto> result = new ArrayList<>();
        for (Map.Entry<String, List<TutorTimeslot>> entry : slotsByTutorId.entrySet()) {
            String tutorId = entry.getKey();
            List<TutorTimeslot> tutorSlots = entry.getValue();

            if (tutorSlots.size() != dayOfWeeks.size()) {
                continue;
            }

            List<MatchedSlotDto> matched = new ArrayList<>();
            for (TutorTimeslot slot : tutorSlots) {
                matched.add(new MatchedSlotDto(
                        slot.getDayOfWeek(),
                        slot.getStartTime(),
                        slot.getEndTime()
                ));
            }

            result.add(new AvailableTutorDto(tutorId, matched));
        }

        return result;
    }
}
