package com.douzone.vebs.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "수강 가능 강사 정보")
public class AvailableTutorDto {

    @Schema(description = "강사 ID", example = "tutor1")
    private String tutorId;

    @Schema(description = "해당 강사의 매칭된 가능 시간대 목록")
    private List<MatchedSlotDto> matchedSlots;
}
