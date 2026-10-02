package org.gk.flatirons.assessment.ims.handlers;

import org.gk.flatirons.assessment.common.dto.queue.NotificationRequest;
import org.gk.flatirons.assessment.common.utils.ObjectUtils;
import org.gk.flatirons.assessment.common.utils.RabbitMqUtils;
import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.gk.flatirons.assessment.ims.entity.Interviewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
        Set<String> toMailIds = new HashSet<>();
        toMailIds.add(interview.getCandidate().getEmail());
        toMailIds.addAll(interview.getInterviewers().stream().map(Interviewer::getEmail).toList());
        rabbitMqUtils.send("interview.mail.notify", ObjectUtils.toJson(new NotificationRequest(toMailIds, Collections.emptySet(), "FaceToFace Interview Invite", "Dummy Body")));
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
