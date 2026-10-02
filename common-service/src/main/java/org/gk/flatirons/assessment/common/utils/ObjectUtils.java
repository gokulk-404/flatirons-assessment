package org.gk.flatirons.assessment.common.utils;

import tools.jackson.databind.ObjectMapper;
import org.gk.flatirons.assessment.common.exception.dto.customExceptions.JsonProcessingException;
import org.springframework.stereotype.Component;

@Component
public final class ObjectUtils {

    private static ObjectMapper objectMapper;

    public ObjectUtils(ObjectMapper objectMapper) {
        ObjectUtils.objectMapper = objectMapper;
    }

    public static String toJson(Object source) throws JsonProcessingException {
        try {
           return objectMapper.writeValueAsString(source);
        } catch (Exception exception) {
            throw new JsonProcessingException(exception.getMessage());
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception exception) {
            throw new JsonProcessingException(
                    "Failed to deserialize JSON to " + clazz.getSimpleName(), exception);
        }
    }
}
