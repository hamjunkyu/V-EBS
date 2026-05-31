package com.douzone.vebs.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
@AllArgsConstructor
@Schema(description = "에러 응답")
public class ErrorResponseDto {

    @Schema(description = "에러 코드", example = "TUTOR_CONFLICT")
    private String errorCode;

    @Schema(description = "에러 메시지", example = "강사가 이미 다른 수업과 시간이 겹칩니다")
    private String message;
}
