package org.gk.flatirons.assessment.ims.dto.response;

import org.gk.flatirons.assessment.ims.constant.InterviewMode;
import org.gk.flatirons.assessment.ims.constant.InterviewRound;
import org.gk.flatirons.assessment.ims.constant.InterviewStatus;

import java.time.Instant;
import java.util.List;

public record InterviewResponse(
        Integer id,
        Integer candidateId,
        String candidateName,
        List<InterviewerDetail> interviewers,
        InterviewRound round,
        InterviewMode mode,
        InterviewStatus status,
        Instant scheduledStart,
        Instant scheduledEnd
) {}
