package org.gk.flatirons.assessment.common.exception.dto.customExceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Object id) {
        super("Resource " + resourceName + " with id/s " + id + " not found");
    }
}
