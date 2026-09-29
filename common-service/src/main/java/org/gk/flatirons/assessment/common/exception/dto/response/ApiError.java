package org.gk.flatirons.assessment.common.exception.dto.response;

import java.time.Instant;
import java.util.List;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<String> details
) {
}
