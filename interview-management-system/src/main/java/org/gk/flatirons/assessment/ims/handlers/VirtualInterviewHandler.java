package org.gk.flatirons.assessment.ims.handlers;

import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.entity.Interview;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class VirtualInterviewHandler implements InterviewModeBaseHandler {

    private static final Logger logger = LoggerFactory.getLogger(VirtualInterviewHandler.class);

    private void generateMeetingLink(Interview interview) {
        logger.info("Generating meeting link for interview {}", interview.getId());
    }

    private void triggerMailNotificationToParticipants(Interview interview) {
        logger.info("Triggering mail notification to participant {}", interview.getId());
    }

    @Override
    public InterviewMode mode() {
        return InterviewMode.VIRTUAL;
    }

    @Override
    public void prepare(Interview interview) {
        generateMeetingLink(interview);
        triggerMailNotificationToParticipants(interview);
    }
}
