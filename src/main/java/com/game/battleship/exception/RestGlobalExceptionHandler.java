package com.game.battleship.exception;

import com.game.battleship.dto.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.game.battleship.util.Commons.ErrorCodes.PAGE_NOT_FOUND_ERROR;
import static com.game.battleship.util.Commons.ErrorCodes.PAGE_UNKNOWN_ERROR;
import static com.game.battleship.util.Commons.ErrorCodes.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class RestGlobalExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleValidationException(HandlerMethodValidationException ex) {
        log.atError()
                .setMessage("[ERROR ValidationException] {}: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .log();
        return ErrorResponseDto.builder()
                .statusCode(VALIDATION_ERROR)
                .message("Validation failed")
                .errorType("ValidationError")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleGeneralException(Exception ex) {
        log.atError()
                .setMessage("[ERROR Exception] {}: {}. Stack trace: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .addArgument(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    return sb.toString();
                })
                .log();
        return ErrorResponseDto.builder()
                .statusCode(PAGE_UNKNOWN_ERROR)
                .message("An unexpected error occurred")
                .errorType("InternalServerError")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleRuntimeException(RuntimeException ex) {
        log.atError()
                .setMessage("[ERROR RuntimeException] {}: {}. Stack trace: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .addArgument(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    return sb.toString();
                })
                .log();
        return ErrorResponseDto.builder()
                .statusCode(PAGE_UNKNOWN_ERROR)
                .message("An unexpected error occurred")
                .errorType("InternalServerError")
                .timestamp(System.currentTimeMillis())
                .build();
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNoResourceFoundException(NoResourceFoundException ex) {
        log.atError()
                .setMessage("[ERROR NoResourceFoundException] {}: {}. Stack trace: {}")
                .addArgument(ex.getClass().getName())
                .addArgument(ex.getMessage())
                .addArgument(() -> {
                    StringBuilder sb = new StringBuilder();
                    for (StackTraceElement element : ex.getStackTrace()) {
                        sb.append(element.toString()).append("\n");
                    }
                    return sb.toString();
                })
                .log();
        return ErrorResponseDto.builder()
                .statusCode(PAGE_NOT_FOUND_ERROR)
                .message("Resource not found")
                .errorType("NotFound")
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
