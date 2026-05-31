package com.douzone.vebs.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@Schema(description = "수강신청 응답")
public class AttendanceResponseDto {

    @Schema(description = "수강신청 ID", example = "7")
    private Long attSeq;

    @Schema(description = "수업 종료일", example = "2026-08-12")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "총 수업 횟수", example = "32")
    private Integer lessonCount;

    @Schema(description = "배정된 강사 ID", example = "tutor1")
    private String assignedTutorId;
}
