package org.gk.flatirons.assessment.common.exception.dto.customExceptions;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }
}
