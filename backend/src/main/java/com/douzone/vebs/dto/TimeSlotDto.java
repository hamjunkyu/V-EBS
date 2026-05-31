package com.douzone.vebs.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Schema(description = "수강 가능 시간 슬롯 정보")
public class TimeSlotDto {

    @Schema(description = "시간 슬롯", example = "13:00")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    @Schema(description = "신청 가능 여부", example = "true")
    private Boolean available;

    @Schema(description = "자동 배정될 강사 ID (불가 시 null)", example = "tutor1")
    private String assignedTutorId;
}
