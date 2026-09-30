package org.gk.flatirons.assessment.ims.dto.response;

import org.gk.flatirons.assessment.ims.constant.FeedbackStatus;

public record FeedbackResponse(
        Integer id,
        Integer interviewId,
        Integer interviewerId,
        String interviewerName,
        Integer rating,
        String comments,
        FeedbackStatus status
) {
}