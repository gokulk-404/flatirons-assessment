package org.gk.flatirons.assessment.common.exception.dto.customExceptions;

public class SchedulingConflictException extends RuntimeException {

    public SchedulingConflictException(String message) {
        super(message);
    }
}
