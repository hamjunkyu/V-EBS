package com.douzone.vebs.exception;

import com.douzone.vebs.dto.ErrorResponseDto;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.util.List;
import java.util.ArrayList;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TutorConflictException.class)
    public ResponseEntity<ErrorResponseDto> handleTutorConflict(TutorConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto("TUTOR_CONFLICT", e.getMessage()));
    }

    @ExceptionHandler(NoAvailableTutorException.class)
    public ResponseEntity<ErrorResponseDto> handleNoAvailableTutor(NoAvailableTutorException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponseDto("NO_AVAILABLE_TUTOR", e.getMessage()));
    }

    @ExceptionHandler(InvalidStartDateException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidStartDate(InvalidStartDateException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto("INVALID_START_DATE", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto("INVALID_INPUT", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(MethodArgumentNotValidException e) {
        List<FieldError> errors = e.getBindingResult().getFieldErrors();
        List<String> messages = new ArrayList<>();
        for (FieldError err : errors) {
            messages.add(err.getField() + ": " + err.getDefaultMessage());
        }
        String message = String.join(", ", messages);
        return ResponseEntity.badRequest()
                .body(new ErrorResponseDto("VALIDATION_ERROR", message));
    }
}
