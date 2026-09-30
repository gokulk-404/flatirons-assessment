package org.gk.flatirons.assessment.ims.handlers;

import org.gk.flatirons.assessment.common.utils.RabbitMqUtils;
import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FaceToFaceInterviewHandler implements InterviewModeBaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(FaceToFaceInterviewHandler.class);

    private final RabbitMqUtils rabbitMqUtils;

    public FaceToFaceInterviewHandler(RabbitMqUtils rabbitMqUtils) {
        this.rabbitMqUtils = rabbitMqUtils;
    }

    private void generateGatePass(Interview interview) {
        logger.info("Creating gate pass for interview {}", interview.getId());
    }

    private void triggerMailNotificationToParticipants(Interview interview) {
        logger.info("Triggering mail notification to participant {}", interview.getId());
        rabbitMqUtils.send("interview.mail.notify", "Trigger Face to Face Interview Mail Notification");
    }

    @Override
    public InterviewMode mode() {
        return InterviewMode.FACE_TO_FACE;
    }

    @Override
    public void prepare(Interview interview) {
        generateGatePass(interview);
        triggerMailNotificationToParticipants(interview);
    }
}
