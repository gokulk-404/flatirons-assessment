package org.gk.flatirons.assessment.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.ResourceNotFoundException;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.SchedulingConflictException;
import org.gk.flatirons.assessment.common.exception.dto.response.ApiError;
import org.gk.flatirons.assessment.common.exception.utils.ExceptionResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ExceptionResponseMapper exceptionResponseMapper;

    public GlobalExceptionHandler(ExceptionResponseMapper exceptionResponseMapper) {
        this.exceptionResponseMapper = exceptionResponseMapper;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException notFoundException, HttpServletRequest request) {
        return exceptionResponseMapper.mapToExceptionResponse(HttpStatus.NOT_FOUND, notFoundException.getMessage(), request, null);
    }

    @ExceptionHandler(SchedulingConflictException.class)
    public ResponseEntity<ApiError> handleConflict(SchedulingConflictException conflictException, HttpServletRequest request) {
        return exceptionResponseMapper.mapToExceptionResponse(HttpStatus.CONFLICT, conflictException.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request) {
        List<String> details = methodArgumentNotValidException.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return exceptionResponseMapper.mapToExceptionResponse(HttpStatus.BAD_REQUEST, "Validation failed", request, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception on {}", request.getRequestURI(), ex);
        return exceptionResponseMapper.mapToExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, null);
    }

}
