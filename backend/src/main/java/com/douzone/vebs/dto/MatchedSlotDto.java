package com.douzone.vebs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
@Schema(description = "매칭된 강사의 가능 시간대")
public class MatchedSlotDto {

    @Schema(description = "요일 (1=월, 2=화, 3=수, 4=목, 5=금)", example = "1")
    private Integer dayOfWeek;

    @Schema(description = "강사 가능 시작시간", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "강사 가능 종료시간", example = "13:00:00")
    private LocalTime endTime;
}
