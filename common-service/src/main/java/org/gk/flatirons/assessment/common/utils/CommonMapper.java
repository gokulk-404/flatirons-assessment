package org.gk.flatirons.assessment.common.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.gk.flatirons.assessment.common.exception.dto.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public final class CommonMapper {

    public static ResponseEntity<ApiError> mapToExceptionResponse(HttpStatus status, String message, HttpServletRequest request, List<String> details) {
        ApiError body = new ApiError(Instant.now(), status.value(), status.getReasonPhrase(), message, request.getRequestURI(), details);
        return ResponseEntity.status(status).body(body);
    }
}
