package org.gk.flatirons.assessment.common.utils;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqUtils {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMqUtils(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String queueName, String requestBody) {
        rabbitTemplate.convertAndSend(queueName, requestBody);
    }
}
