package com.douzone.vebs.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "수강신청 요청")
public class AttendanceRequestDto {

    @Schema(description = "학생 ID", example = "stu100")
    @NotBlank
    private String studentId;

    @Schema(description = "학생 고유번호", example = "9999")
    @NotNull
    private Integer studentSeq;

    @Schema(description = "수업 시작일", example = "2026-06-01")
    @NotNull
    @FutureOrPresent
    private LocalDate startDate;

    @Schema(description = "요일 패턴 (1=월수금, 2=화목)", example = "1")
    @NotNull
    @Min(1) @Max(2)
    private Integer dayPattern;

    @Schema(description = "수업 과정 (1=스마트파닉스, 2=여행영어, 3=프리토킹)", example = "1")
    @NotNull
    @Min(1) @Max(3)
    private Integer courseType;

    @Schema(description = "수업 시작 시간", example = "13:00")
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
}
