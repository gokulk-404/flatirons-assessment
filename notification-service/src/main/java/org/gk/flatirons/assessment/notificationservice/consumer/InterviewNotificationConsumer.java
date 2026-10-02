package org.gk.flatirons.assessment.notificationservice.consumer;

import org.gk.flatirons.assessment.common.dto.queue.NotificationRequest;
import org.gk.flatirons.assessment.common.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InterviewNotificationConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(InterviewNotificationConsumer.class);

    @RabbitListener(queuesToDeclare = {@Queue("interview.mail.notify")})
    public void interviewMailNotifyListener(String requestBody) {
        try {
            NotificationRequest  notificationRequest = ObjectUtils.fromJson(requestBody, NotificationRequest.class);
            LOGGER.info("Interview Mail Notify Listener with : {}", notificationRequest);
        } catch (Exception e) {
            LOGGER.error("Error while processing Interview Mail Notify Listener", e);
        }
    }
}
